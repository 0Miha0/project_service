package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomentService {

    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final ProjectService projectService;
    private final List<Filter<Moment, MomentFilterDto>> momentFilters;

    public MomentDto createMoment(MomentDto momentDto) {
        log.info("Creating a new moment: {}", momentDto);
        Moment moment = momentMapper.toEntity(momentDto);
        validateProjectIsActive(moment);
        moment.setProjects(projectService.findProjectsByIds(momentDto.getProjectIds()));
        Moment savedMoment = momentRepository.save(moment);
        log.info("Moment created with ID: {}", savedMoment.getId());
        return momentMapper.toDto(savedMoment);
    }

    public MomentDto updateMoment(MomentDto updatedMomentDto, Long momentId) {
        log.info("Updating moment with ID: {} using data: {}", momentId, updatedMomentDto);
        Moment momentToUpdate = findById(momentId);
        log.debug("Found existing moment: {}", momentToUpdate);

        Moment updatedMoment = momentMapper.toEntity(updatedMomentDto);
        validateProjectIsActive(updatedMoment);

        momentMapper.updateEntity(momentToUpdate, updatedMomentDto);
        momentToUpdate.setProjects(projectService.findProjectsByIds(updatedMomentDto.getProjectIds()));
        momentToUpdate.setUpdatedAt(LocalDateTime.now());
        Moment savedMoment = momentRepository.save(momentToUpdate);

        log.info("Moment updated successfully with ID: {}", savedMoment.getId());
        return momentMapper.toDto(savedMoment);
    }

    public List<MomentDto> getMomentsByFilter(MomentFilterDto filters) {
        log.info("Retrieving moments using filters: {}", filters);
        Stream<Moment> moments = momentRepository.findAll().stream();
        List<MomentDto> filteredMoments = momentFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(moments, (stream, filter) -> filter.apply(stream, filters), (s1, s2) -> s1)
                .map(momentMapper::toDto)
                .toList();
        log.info("Found {} moments matching the filters", filteredMoments.size());
        return filteredMoments;
    }

    public List<MomentDto> getAllMoments() {
        log.info("Retrieving all moments");
        List<MomentDto> moments = momentRepository.findAll().stream()
                .map(momentMapper::toDto)
                .toList();
        log.info("Found {} moments", moments.size());
        return moments;
    }

    public MomentDto getMomentById(Long id) {
        log.info("Retrieving moment with ID: {}", id);
        Moment moment = findById(id);
        return momentMapper.toDto(moment);
    }

    public Moment findById(Long id) {
        return momentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moment not found with ID: " + id));
    }

    private void validateProjectIsActive(Moment moment) {
        log.debug("Validating if moment is active: {}", moment);
        if (moment.getName() == null || moment.getName().isEmpty()) {
            log.error("Moment name cannot be empty");
            throw new IllegalArgumentException("Moment name cannot be empty");
        }
        if (moment.getProjects() != null && moment.getProjects().stream().anyMatch(
                project -> project.getStatus().equals(ProjectStatus.COMPLETED)
                        || project.getStatus().equals(ProjectStatus.ON_HOLD)
                        || project.getStatus().equals(ProjectStatus.CANCELLED))) {
            log.error("Moment cannot be created for inactive projects: {}", moment.getProjects());
            throw new IllegalArgumentException("Moment can only be created for active projects");
        }
        log.debug("Moment is valid: {}", moment);
    }
}