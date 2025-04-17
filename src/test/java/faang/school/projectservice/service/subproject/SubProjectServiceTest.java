package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.filter.subproject.SubProjectFilter;
import faang.school.projectservice.filter.subproject.SubProjectNameFilter;
import faang.school.projectservice.filter.subproject.SubProjectStatusFilter;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.subproject.SubProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubProjectServiceTest {

    @Spy
    private final SubProjectMapper subProjectMapper = Mappers.getMapper(SubProjectMapper.class);

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SubProjectValidator subProjectValidator;

    @InjectMocks
    private SubProjectService subProjectService;

    @BeforeEach
    public void setUp() {
        List<SubProjectFilter> subProjectFilters = List.of(
                new SubProjectNameFilter(),
                new SubProjectStatusFilter()
        );
        ReflectionTestUtils.setField(subProjectService, "subProjectFilters", subProjectFilters);
    }

    @Test
    @DisplayName("Test find sub projects with existent parent id")
    public void findSubProjectsWithExistentParentIdTest() {
        Long parentId = 1L;
        Long subProjectId = 2L;
        Long userId = 2L;
        SubProjectFilterDto filters = new SubProjectFilterDto();
        Project subProject = Project.builder()
                .id(subProjectId)
                .teams(List.of(Team.builder().teamMembers(List.of(TeamMember.builder().userId(userId).build())).build()))
                .build();
        Project project = Project.builder()
                .id(parentId)
                .children(List.of(subProject))
                .build();
        when(projectRepository.getProjectById(parentId)).thenReturn(project);

        List<ProjectDto> result = subProjectService.findSubProjects(parentId, filters, userId);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Test find sub projects with nonexistent parent id")
    public void findSubProjectsWithNonexistentParentIdTest() {
        Long parentId = 100L;
        Long userId = 2L;
        SubProjectFilterDto filters = new SubProjectFilterDto();
        when(projectRepository.getProjectById(parentId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> subProjectService.findSubProjects(parentId, filters, userId));
    }

    @Test
    @DisplayName("Test find sub projects with status filter")
    public void findSubProjectsWithStatusFilterTest() {
        Long parentId = 1L;
        Long subProjectId = 2L;
        Long userId = 2L;
        SubProjectFilterDto filters = SubProjectFilterDto.builder().status(ProjectStatus.CANCELLED).build();
        Project subProject = Project.builder()
                .id(subProjectId)
                .teams(List.of(Team.builder().teamMembers(List.of(TeamMember.builder().userId(userId).build())).build()))
                .status(ProjectStatus.CANCELLED)
                .build();
        Project project = Project.builder()
                .id(parentId)
                .children(List.of(subProject))
                .build();
        when(projectRepository.getProjectById(parentId)).thenReturn(project);

        List<ProjectDto> result = subProjectService.findSubProjects(parentId, filters, userId);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Test find sub projects with name filter")
    public void findSubProjectsWithNameFilterTest() {
        Long parentId = 1L;
        Long subProjectId = 2L;
        Long userId = 2L;
        SubProjectFilterDto filters = SubProjectFilterDto.builder().namePattern("Dev").build();
        Project subProject = Project.builder()
                .id(subProjectId)
                .teams(List.of(Team.builder().teamMembers(List.of(TeamMember.builder().userId(userId).build())).build()))
                .name("Developer")
                .build();
        Project project = Project.builder()
                .id(parentId)
                .children(List.of(subProject))
                .build();
        when(projectRepository.getProjectById(parentId)).thenReturn(project);

        List<ProjectDto> result = subProjectService.findSubProjects(parentId, filters, userId);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Test find sub projects with private project")
    public void findSubProjectsWithPrivateProjectTest() {
        Long parentId = 1L;
        Long subProjectId = 2L;
        Long userId = 2L;
        SubProjectFilterDto filters = new SubProjectFilterDto();
        Project subProject = Project.builder()
                .id(subProjectId)
                .name("Developer")
                .teams(List.of(Team.builder().teamMembers(List.of(TeamMember.builder().userId(3L).build())).build()))
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        Project project = Project.builder()
                .id(parentId)
                .children(List.of(subProject))
                .build();
        when(projectRepository.getProjectById(parentId)).thenReturn(project);

        List<ProjectDto> result = subProjectService.findSubProjects(parentId, filters, userId);

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test create sub project with existent parent id and valid dto")
    public void createSubProjectTest() {
        Long parentId = 1L;
        Long userId = 1L;
        CreateSubProjectDto dto = CreateSubProjectDto.builder()
                .name("cool name")
                .description("cool description")
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project parentProject = new Project();
        Project project = Project.builder()
                .ownerId(1L)
                .parentProject(parentProject)
                .name("cool name")
                .description("cool description")
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CREATED)
                .build();

        when(projectRepository.getProjectById(parentId)).thenReturn(parentProject);
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto result = subProjectService.createSubProject(parentId, dto, userId);

        assertEquals(userId, result.getOwnerId());
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getVisibility(), result.getVisibility());
        assertEquals(ProjectStatus.CREATED, result.getStatus());
    }

    @Test
    @DisplayName("Test create sub project with nonexistent parentId")
    public void createSubProjectWithNonexistentParentIdTest() {
        Long parentId = 1L;
        Long userId = 2L;
        when(projectRepository.getProjectById(parentId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> subProjectService.createSubProject(parentId, new CreateSubProjectDto(), userId));
    }

    @Test
    @DisplayName("Test create sub project with nonexistent owner")
    public void createSubProjectWithNonexistentOwnerTest() {
        Long parentId = 1L;
        Long userId = 11L;
        CreateSubProjectDto dto = CreateSubProjectDto.builder()
                .name("cool name")
                .description("cool description")
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project parentProject = new Project();
        when(projectRepository.getProjectById(parentId)).thenReturn(parentProject);
        doThrow(EntityNotFoundException.class).when(subProjectValidator).validateOwnerExistence(userId);

        assertThrows(EntityNotFoundException.class, () -> subProjectService.createSubProject(parentId, dto, userId));
    }

    @Test
    @DisplayName("Test create sub project with existent name")
    public void createSubProjectWithExistentNameTest() {
        Long parentId = 1L;
        Long userId = 2L;
        CreateSubProjectDto dto = CreateSubProjectDto.builder()
                .name("cool name")
                .description("cool description")
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project parentProject = new Project();
        when(projectRepository.getProjectById(parentId)).thenReturn(parentProject);
        doThrow(EntityNotFoundException.class)
                .when(subProjectValidator).validateExistenceByOwnerIdAndName(userId, dto.getName());

        assertThrows(EntityNotFoundException.class, () -> subProjectService.createSubProject(parentId, dto, userId));
    }

    @Test
    @DisplayName("Test create sub project with invalid visibility")
    public void createSubProjectWithInvalidVisibilityTest() {
        Long parentId = 1L;
        Long userId = 2L;
        CreateSubProjectDto dto = CreateSubProjectDto.builder()
                .name("cool name")
                .description("cool description")
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project parentProject = new Project();
        when(projectRepository.getProjectById(parentId)).thenReturn(parentProject);
        doThrow(EntityNotFoundException.class)
                .when(subProjectValidator).validateSubProjectVisibility(parentProject.getVisibility(), dto.getVisibility());

        assertThrows(EntityNotFoundException.class, () -> subProjectService.createSubProject(parentId, dto, userId));
    }

    @Test
    @DisplayName("Test update sub project")
    public void updateSubProjectTest() {
        Long parentId = 1L;
        Long subProjectId = 2L;
        ProjectDto dto = ProjectDto.builder()
                .name("new cool name")
                .description("new cool description")
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Long userId = 1L;
        Project parentProject = Project.builder()
                .id(parentId)
                .build();
        Project subProject = Project.builder()
                .id(subProjectId)
                .parentProject(parentProject)
                .name("cool name")
                .description("cool description")
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.CREATED)
                .build();
        when(projectRepository.getProjectById(parentId)).thenReturn(parentProject);
        when(projectRepository.getProjectById(subProjectId)).thenReturn(subProject);
        when(projectRepository.save(subProject)).thenReturn(subProject);

        ProjectDto result = subProjectService.updateSubProject(parentId, subProjectId, dto, userId);

        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getVisibility(), result.getVisibility());
        assertEquals(dto.getStatus(), result.getStatus());

    }

    @Test
    @DisplayName("Test update sub project with nonexistent parent id")
    public void updateSubProjectWithNonexistentParentIdTest() {
        Long parentId = 2L;
        Long subProjectId = 3L;
        ProjectDto dto = new ProjectDto();
        Long userId = 1L;
        when(projectRepository.getProjectById(parentId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> subProjectService.updateSubProject(parentId, subProjectId, dto, userId));
    }

    @Test
    @DisplayName("Test update sub project with nonexistent sub project id")
    public void updateSubProjectWithNonexistentSubProjectIdTest() {
        Long parentId = 2L;
        Long subProjectId = 3L;
        ProjectDto dto = new ProjectDto();
        Long userId = 1L;
        Project parentProject = new Project();
        when(projectRepository.getProjectById(parentId)).thenReturn(parentProject);
        when(projectRepository.getProjectById(subProjectId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> subProjectService.updateSubProject(parentId, subProjectId, dto, userId));
    }

    @Test
    @DisplayName("Test update sub project with sub project not from project")
    public void updateSubProjectWithSubProjectNotFromProjectTest() {
        Long parentId = 2L;
        Long subProjectId = 3L;
        ProjectDto dto = new ProjectDto();
        Long userId = 1L;
        Project parentProject = new Project();
        Project subProject = new Project();
        when(projectRepository.getProjectById(parentId)).thenReturn(parentProject);
        when(projectRepository.getProjectById(subProjectId)).thenReturn(subProject);
        doThrow(IllegalArgumentException.class)
                .when(subProjectValidator).validateSubProjectBelonging(parentId, subProject);

        assertThrows(IllegalArgumentException.class,
                () -> subProjectService.updateSubProject(parentId, subProjectId, dto, userId));
    }

    @Test
    @DisplayName("Test update sub project with invalid ownership")
    public void updateSubProjectWithInvalidOwnershipTest() {
        Long parentId = 2L;
        Long subProjectId = 3L;
        ProjectDto dto = new ProjectDto();
        Long userId = 1L;
        Project parentProject = new Project();
        Project subProject = Project.builder()
                .ownerId(30L)
                .build();
        when(projectRepository.getProjectById(parentId)).thenReturn(parentProject);
        when(projectRepository.getProjectById(subProjectId)).thenReturn(subProject);
        doThrow(IllegalArgumentException.class)
                .when(subProjectValidator).validateOwnership(userId, subProject.getOwnerId());

        assertThrows(IllegalArgumentException.class,
                () -> subProjectService.updateSubProject(parentId, subProjectId, dto, userId));
    }

    @Test
    @DisplayName("Test update sub project with existent project name")
    public void updateSubProjectWithExistentProjectNameTest() {
        Long parentId = 2L;
        Long subProjectId = 3L;
        ProjectDto dto = ProjectDto.builder()
                .ownerId(2L)
                .name("new cool name")
                .build();
        Long userId = 1L;
        Project parentProject = new Project();
        Project subProject = Project.builder()
                .ownerId(2L)
                .build();
        when(projectRepository.getProjectById(parentId)).thenReturn(parentProject);
        when(projectRepository.getProjectById(subProjectId)).thenReturn(subProject);
        doThrow(IllegalArgumentException.class)
                .when(subProjectValidator).validateExistenceByOwnerIdAndName(dto.getOwnerId(), dto.getName());

        assertThrows(IllegalArgumentException.class,
                () -> subProjectService.updateSubProject(parentId, subProjectId, dto, userId));
    }
}
