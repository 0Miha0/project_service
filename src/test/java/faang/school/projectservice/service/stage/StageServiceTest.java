package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.ActionWithTaskDto;
import faang.school.projectservice.dto.stage.StageDeleteDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.mapper.stage.StageMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.validator.stage.StageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageServiceTest {
    @InjectMocks
    private StageService stageService;

    @Mock
    private ProjectService projectService;

    @Mock
    private StageJpaRepository stageRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StageRolesRepository stageRolesRepository;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private TeamMemberService teamMemberService;

    @Spy
    private StageMapperImpl stageMapper;

    @Mock
    private StageValidator stageValidator;

    @Mock
    private StageFilter stageFilter;

    @Mock
    List<StageFilter> filters;

    private Stage stage;
    private StageDto stageDto;
    private StageFilterDto stageFilterDto;
    private Project project;
    private List<Task> tasks;


    @BeforeEach
    public void setUp() {
        stageFilterDto = StageFilterDto.builder()
                .build();

        stageFilter = mock(StageFilter.class);

        filters = List.of(stageFilter);

        List<TeamMember> teamMembers = List.of(
                TeamMember.builder()
                        .id(1L)
                        .roles(List.of(TeamRole.DESIGNER, TeamRole.DEVELOPER, TeamRole.TESTER))
                        .build(),
                TeamMember.builder()
                        .id(2L)
                        .roles(List.of(TeamRole.DESIGNER, TeamRole.DEVELOPER, TeamRole.TESTER))
                        .build(),
                TeamMember.builder()
                        .id(3L)
                        .roles(List.of(TeamRole.DESIGNER, TeamRole.DEVELOPER, TeamRole.TESTER))
                        .build()
        );

        Team team = Team.builder()
                .id(1L)
                .teamMembers(teamMembers)
                .build();

        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .teams(List.of(team))
                .build();

        List<StageRoles> stageRoles = List.of(
                StageRoles.builder().id(1L).count(10).teamRole(TeamRole.DESIGNER).build(),
                StageRoles.builder().id(2L).count(10).teamRole(TeamRole.DEVELOPER).build(),
                StageRoles.builder().id(3L).count(10).teamRole(TeamRole.TESTER).build()
        );

        tasks = List.of(
                Task.builder().id(1L).description("Task 1").build(),
                Task.builder().id(2L).description("Task 2").build()
        );

        List<TeamMember> executors = List.of(
                TeamMember.builder().id(1L).roles(List.of(TeamRole.DESIGNER)).build(),
                TeamMember.builder().id(2L).roles(List.of(TeamRole.DEVELOPER)).build(),
                TeamMember.builder().id(3L).roles(List.of(TeamRole.TESTER)).build()
        );

        stage = Stage.builder()
                .stageId(1L)
                .stageName("Test Stage")
                .project(project)
                .stageRoles(stageRoles)
                .tasks(tasks)
                .executors(executors)
                .build();

        project.setStages(List.of(stage));

        stageDto = StageDto.builder()
                .stageId(1L)
                .stageName("Test Stage")
                .projectId(1L)
                .stageRolesId(List.of(1L, 2L, 3L))
                .executorsId(List.of(1L, 2L, 3L))
                .build();
    }

    @Test
    @DisplayName("Verifying successful stage creation")
    public void checkCreateStageSuccessTest() {
        when(stageRolesRepository.findAllById(stageDto.getStageRolesId()))
                .thenReturn(stage.getStageRoles());
        when(projectService.getProjectById(stageDto.getProjectId()))
                .thenReturn(stage.getProject());
        when(teamMemberService.findAllById(stageDto.getExecutorsId()))
                .thenReturn(stage.getExecutors());
        when(stageRepository.save(any(Stage.class)))
                .thenAnswer(invocation -> {
                    Stage saved = invocation.getArgument(0);
                    saved.setStageId(1L);
                    return saved;
                });

        StageDto createdStage = stageService.createStage(stageDto);

        assertNotNull(createdStage);
        assertEquals(1L, createdStage.getStageId());

        verify(stageRepository, times(1)).save(any(Stage.class));
    }

    @Test
    @DisplayName("Verifying successful stage acquisition with filtering")
    public void checkGetStageByFilterSuccessTest() {
        StageFilterDto filterDto = StageFilterDto.builder()
                .taskStatusPattern("todo")
                .teamRolePattern("owner")
                .build();

        Stage firstFilter = Stage.builder()
                .tasks(List.of(Task.builder().status(TaskStatus.TODO).build()))
                .stageRoles(List.of(StageRoles.builder().teamRole(TeamRole.OWNER).build()))
                .executors(Collections.emptyList())
                .build();

        Stage secondFilter = Stage.builder()
                .tasks(List.of(Task.builder().status(TaskStatus.TODO).build()))
                .stageRoles(List.of(StageRoles.builder().teamRole(TeamRole.OWNER).build()))
                .executors(Collections.emptyList())
                .build();

        List<Stage> stageList = List.of(firstFilter, secondFilter);

        when(stageRepository.findAll())
                .thenReturn(stageList);

        List<StageDto> result = stageService.getStageByFilter(filterDto);

        assertNotNull(result);

        verify(stageRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Verifying successful user cascade deletion")
    public void checkDeleteUserSuccessCascadeTest() {
        ActionWithTaskDto actionWithTask = ActionWithTaskDto.builder()
                .action("CASCADE")
                .build();

        StageDeleteDto stageDeleteDto = StageDeleteDto.builder()
                .stageId(1L)
                .actionWithTaskDto(actionWithTask)
                .build();

        when(stageRepository.getById(1L))
                .thenReturn(stage);

        when(stageRepository.getById(stage.getStageId()))
                .thenReturn(stage);

        stageService.deleteStage(stageDeleteDto);

        verify(stageRepository, times(1))
                .delete(stage);
    }

    @Test
    @DisplayName("Verifying successful user close deletion")
    public void checkDeleteUserSuccessCloseTest() {
        ActionWithTaskDto actionWithTask = ActionWithTaskDto.builder()
                .action("CLOSE")
                .build();

        StageDeleteDto stageDeleteDto = StageDeleteDto.builder()
                .stageId(1L)
                .actionWithTaskDto(actionWithTask)
                .build();

        when(stageRepository.getById(1L))
                .thenReturn(stage);

        stageService.deleteStage(stageDeleteDto);

        verify(stageRepository, times(1))
                .delete(stage);
    }

    @Test
    @DisplayName("Verifying successful transfer deletion")
    public void checkDeleteUserSuccessTransferTest() {
        ActionWithTaskDto actionWithTask = ActionWithTaskDto.builder()
                .action("TRANSFER")
                .transferStageId(2L)
                .build();

        StageDeleteDto stageDeleteDto = StageDeleteDto.builder()
                .stageId(1L)
                .actionWithTaskDto(actionWithTask)
                .build();

        Stage deletedStage = Stage.builder()
                .stageId(1L)
                .stageName("Test Stage")
                .project(project)
                .stageRoles(stage.getStageRoles())
                .tasks(tasks)
                .executors(Collections.emptyList())
                .build();

        Stage transferStage = Stage.builder()
                .stageId(2L)
                .stageName("Transferred Stage")
                .project(project)
                .stageRoles(stage.getStageRoles())
                .tasks(Collections.emptyList())
                .executors(Collections.emptyList())
                .build();

        Stage savedTransferStage = Stage.builder()
                .stageId(2L)
                .stageName("Transferred Stage")
                .project(project)
                .stageRoles(stage.getStageRoles())
                .tasks(tasks)
                .executors(Collections.emptyList())
                .build();

        when(stageRepository.getById(stage.getStageId()))
                .thenReturn(deletedStage);
        when(stageRepository.getById(transferStage.getStageId()))
                .thenReturn(transferStage);
        when(stageRepository.save(any(Stage.class)))
                .thenReturn(savedTransferStage);

        stageService.deleteStage(stageDeleteDto);

        verify(stageRepository, times(1)).delete(deletedStage);
        verify(stageRepository, times(1)).save(transferStage);
    }

    @Test
    @DisplayName("Verifying successful stage update")
    public void checkUpdateStageSuccessTest() {
        StageInvitation stageInvitation = StageInvitation.builder()
                .stage(stage)
                .invited(TeamMember.builder().id(1L).build())
                .status(StageInvitationStatus.PENDING)
                .build();

        when(stageRepository.getById(stage.getStageId()))
                .thenReturn(stage);
        when(stageInvitationRepository.save(any(StageInvitation.class)))
                .thenReturn(stageInvitation);
        when(stageRepository.save(stage))
                .thenReturn(stage);
        when(stageMapper.toDto(stage))
                .thenReturn(stageDto);

        StageDto updatedStage = stageService.updateStage(stageDto);

        assertNotNull(updatedStage);

        verify(stageRepository, times(1))
                .getById(stage.getStageId());
        verify(stageRepository, times(1))
                .save(stage);
    }

    @Test
    @DisplayName("Verification of successful receipt of all stages of the project")
    public void checkGetStagesByProjectSuccessTest() {
        when(projectService.getProjectById(anyLong()))
                .thenReturn(project);
        when(stageMapper.toDto(stage))
                .thenReturn(stageDto);

        List<StageDto> stageDtoList = stageService.getAllProjectStages(project.getId());

        assertNotNull(stageDtoList);

        verify(projectService, times(1))
                .getProjectById(project.getId());
    }

    @Test
    @DisplayName("Checking the successful receipt of a stage by identifier")
    public void checkGetStageByIdSuccessTest() {
        when(stageRepository.getById(stage.getStageId()))
                .thenReturn(stage);
        when(stageMapper.toDto(stage))
                .thenReturn(stageDto);

        StageDto stageDto = stageService.getStageById(stage.getStageId());

        assertNotNull(stageDto);

        verify(stageRepository, times(1))
                .getById(stage.getStageId());
    }

    @Test
    public void stageExists() {
        long id = 1L;
        Stage stage = new Stage();
        stage.setStageId(id);
        when(stageRepository.findById(id)).thenReturn(Optional.of(stage));

        assertDoesNotThrow(() -> stageService.getStageEntity(id));
    }

    @Test
    public void throwsException() {
        long id = 1L;

        assertThrows(EntityNotFoundException.class, () -> stageService.getStageEntity(id));
    }
}