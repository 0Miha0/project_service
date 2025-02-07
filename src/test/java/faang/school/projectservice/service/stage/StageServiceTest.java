package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.ActionWithTaskDto;
import faang.school.projectservice.dto.stage.StageDeleteDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
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
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.StageRolesRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.StageService;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.validator.stage.StageValidator;
import jakarta.persistence.EntityNotFoundException;
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
    private StageRepository stageRepository;

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

    private Stage stage;
    private StageDto stageDto;
    private Project project;
    private List<Task> tasks;


    @BeforeEach
    public void setUp() {
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
    @DisplayName("Verification of successful receipt of all stages of the project")
    public void checkGetStagesByProjectSuccessTest() {
        when(projectService.findById(anyLong()))
                .thenReturn(project);
        when(stageMapper.toDto(stage))
                .thenReturn(stageDto);

        List<StageDto> stageDtoList = stageService.getAllProjectStages(project.getId());

        assertNotNull(stageDtoList);

        verify(projectService, times(1))
                .findById(project.getId());
    }

    @Test
    public void stageExists() {
        long id = 1L;
        Stage stage = new Stage();
        stage.setStageId(id);
        when(stageRepository.findById(id)).thenReturn(Optional.of(stage));

        assertDoesNotThrow(() -> stageService.findById(id));
    }

    @Test
    public void throwsException() {
        long id = 1L;

        assertThrows(EntityNotFoundException.class, () -> stageService.findById(id));
    }
}