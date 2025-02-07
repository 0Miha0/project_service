package faang.school.projectservice.service.project;

import com.amazonaws.services.kms.model.AlreadyExistsException;
import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.project.ProjectNameFilter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    private static final String PROJECT = "Project";

    @Mock
    private ProjectRepository projectRepository;


    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    private final List<Filter<Project,ProjectFilterDto>> projectFilters = new ArrayList<>();

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    public void setUp() {
        projectFilters.add(new ProjectNameFilter());
        projectFilters.add(new ProjectStatusFilter());

        projectService = new ProjectService(projectRepository, projectMapper, projectFilters);
    }

    @Test
    public void findProjectByIdTest() {
        Project project = Project.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        ProjectDto expectedProjectDto = ProjectDto.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectDto result = projectService.findProjectById(1L);

        verify(projectRepository).findById(1L);
        assertEquals(expectedProjectDto.getId(), result.getId());
        assertEquals(expectedProjectDto.getName(), result.getName());
    }

    @Test
    public void findByIdNotFoundTest() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> projectService.findById(1L)
        );
    }

    @Test
    public void findAllProjectsTest() {
        Project project1 = Project.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        Project project2 = Project.builder()
                .id(2L)
                .name("Project2")
                .ownerId(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .description("Description2")
                .build();

        ProjectFilterDto filters = new ProjectFilterDto("Project1", ProjectStatus.CREATED);
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));

        List<ProjectDto> result = projectService.findAllProjects(filters, 1L);

        assertEquals(1, result.size());
        assertEquals(project1.getId(), result.get(0).getId());
    }

    @Test
    public void findAllProjectsNoFilterTest() {
        Project project1 = Project.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        Project project2 = Project.builder()
                .id(2L)
                .name("Project2")
                .ownerId(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .description("Description2")
                .build();

        ProjectFilterDto filters = new ProjectFilterDto(null, null);
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));

        List<ProjectDto> result = projectService.findAllProjects(filters, 1L);

        assertEquals(1, result.size());
        assertEquals(project1.getId(), result.get(0).getId());
    }

    @Test
    public void findAllProjectsNoMatchingFilterTest() {
        Project project1 = Project.builder()
                .id(1L)
                .name("Project1")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .description("Description1")
                .build();

        Project project2 = Project.builder()
                .id(2L)
                .name("Project2")
                .ownerId(2L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .description("Description2")
                .build();

        ProjectFilterDto filters = new ProjectFilterDto("NonExistingProjectName", ProjectStatus.COMPLETED);
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));

        List<ProjectDto> result = projectService.findAllProjects(filters, 1L);

        assertTrue(result.isEmpty());
    }
}