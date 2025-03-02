package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.event_drive.redis.event.ProjectCreateEvent;
import faang.school.projectservice.event_drive.redis.publisher.ProjectCreateEventPublisher;
import faang.school.projectservice.exception.customexception.DataValidationException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    @Value("${project-files.max-project-storage-size}")
    private long maxProjectStorageSize;

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<Filter<Project, ProjectFilterDto>> projectFilters;
    private final ProjectCreateEventPublisher projectCreateEventPublisher;

    @Transactional
    public void createProject(ProjectCreateDto dto, Long userId) {
        log.info("Creating project with name: {}", dto.getName());
        Project project = projectMapper.toCreateEntity(dto);
        project.setOwnerId(userId);
        project.setStatus(ProjectStatus.CREATED);
        project.setMaxStorageSize(new BigInteger(String.valueOf(maxProjectStorageSize)));
        save(project);
        log.info("Project created successfully: {}", dto.getName());

        projectCreateEventPublisher.publish(
                ProjectCreateEvent.builder()
                        .userId(userId)
                        .build()
        );
    }

    @Transactional
    public void updateProject(ProjectUpdateDto dto) {
        log.info("Updating project with id: {}", dto.getId());
        Project project = findById(dto.getId());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());
        project.setUpdatedAt(LocalDateTime.now());
        save(project);
        log.info("Project updated successfully: {}", dto.getId());
    }

    @Transactional(readOnly = true)
    public List<ProjectUpdateDto> findAllProjects(ProjectFilterDto filters, Long userId) {
        List<ProjectUpdateDto> projects = projectRepository.findAll().stream()
                .filter(project -> isProjectVisibleForUser(project, userId))
                .flatMap(project -> applyFilters(project, filters))
                .distinct()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
        log.info("Projects found: {}", projects.size());
        return projects;
    }

    @Transactional
    public ProjectUpdateDto findProjectById(Long projectId) {
        log.info("Finding project by id: {}", projectId);
        return projectMapper.toDto(findById(projectId));
    }

    public Project findById(Long id) {
        log.info("Finding project by id: {}", id);
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
    }

    public void save(Project project) {
        log.info("Saving project: {}", project.getName());
        projectRepository.save(project);
        log.info("Project saved successfully: {}", project.getName());
    }

    public List<Project> findProjectsByIds(List<Long> projectIds) {
        log.info("Finding projects by ids: {}", projectIds);
        return projectRepository.findAllById(projectIds);
    }

    public Project findByIdWithResources(Long projectId) {
        log.info("Finding project by id (including resources) with id: {}", projectId);
        return projectRepository.findByIdWithResources(projectId);
    }

    private boolean isProjectVisibleForUser(Project project, Long userId) {
        return project.getVisibility() == ProjectVisibility.PUBLIC
                || project.getOwnerId().equals(userId);
    }

    private void validateProjectExistsForUser(Long userId, String name) {
        if (projectRepository.existsByOwnerIdAndName(userId, name)) {
            throw new DataValidationException("Project with name '" + name + "' already exists for user with id: " + userId);
        }
    }

    private Stream<Project> applyFilters(Project project, ProjectFilterDto filters) {
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(Stream.of(project),
                        (stream, filter) -> filter.apply(stream, filters),
                        Stream::concat);
    }
}
