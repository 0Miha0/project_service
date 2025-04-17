package faang.school.projectservice.service.task;

import faang.school.projectservice.dto.task.CreateUpdateTaskDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.task.TaskMapper;
import faang.school.projectservice.mapper.task.TaskMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.stage.StageService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.validator.task.TaskValidator;
import faang.school.projectservice.validator.team_member.TeamMemberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Spy
    private TaskMapper taskMapper;

    @Mock
    private StageService stageService;

    @Mock
    private ProjectService projectService;

    @Mock
    private TeamMemberService teamMemberService;

    @Mock
    private TeamMemberValidator teamMemberValidator;

    @Mock
    private TaskValidator taskValidator;

    @Mock
    private Filter<Task, TaskFilterDto> filter;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapperImpl();
        List<Filter<Task, TaskFilterDto>> filters = new ArrayList<>(List.of(filter));

        taskService = new TaskService(filters, taskRepository, taskMapper, taskValidator,
                stageService, projectService, teamMemberService, teamMemberValidator);
    }

    @Test
    public void createTaskTest() {
        long taskCreator = 10L;
        Task parentTask = Task.builder().build();
        Task linkedTask = Task.builder().build();
        Stage stage = Stage.builder().build();
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        CreateUpdateTaskDto taskDto = CreateUpdateTaskDto.builder()
                .name("name")
                .description("description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .reporterUserId(2L)
                .projectId(5L)
                .minutesTracked(10)
                .parentTaskId(2L)
                .linkedTasksIds(new ArrayList<>(List.of(3L)))
                .stageId(8L)
                .build();

        TeamMember teamMember = TeamMember.builder()
                .id(taskCreator)
                .build();

        Project project = Project.builder()
                .id(taskDto.getProjectId())
                .build();

        Task taskToSave = Task.builder()
                .name("name")
                .description("description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .reporterUserId(2L)
                .project(project)
                .minutesTracked(10)
                .parentTask(parentTask)
                .linkedTasks(new ArrayList<>(List.of(linkedTask)))
                .stage(stage)
                .build();

        doNothing().when(taskValidator).validateTaskIdIsNull(taskDto.getId());
        when(teamMemberService.findById(teamMember.getId())).thenReturn(teamMember);
        when(projectService.getProjectById(project.getId())).thenReturn(project);
        doNothing().when(teamMemberValidator).
                validateIsTeamMemberParticipantOfProject(teamMember, project);

        when(taskRepository.findById(taskDto.getParentTaskId())).
                thenReturn(Optional.ofNullable(parentTask));
        when(taskRepository.findById(taskDto.getLinkedTasksIds().get(0))).
                thenReturn(Optional.of(linkedTask));
        when(stageService.getStageEntity(taskDto.getStageId())).thenReturn(stage);

        taskService.createTask(taskDto, taskCreator);

        verify(taskValidator, times(1)).validateTaskIdIsNull(taskDto.getId());
        verify(teamMemberService, times(1)).findById(teamMember.getId());
        verify(projectService).getProjectById(project.getId());
        verify(teamMemberValidator).
                validateIsTeamMemberParticipantOfProject(teamMember, project);
        verify(taskRepository, times(1)).findById(taskDto.getParentTaskId());
        verify(taskRepository, times(1)).findById(taskDto.getLinkedTasksIds().get(0));
        verify(stageService, times(1)).getStageEntity(taskDto.getStageId());
        verify(taskRepository).save(captor.capture());
        Task resultTask = captor.getValue();
        assertEquals(taskToSave, resultTask);
    }

    @Test
    public void updateTaskTest() {
        long taskUpdater = 10L;
        Task parentTask = Task.builder().build();
        Task linkedTask = Task.builder().build();
        Stage stage = Stage.builder().build();
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        CreateUpdateTaskDto taskDto = CreateUpdateTaskDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .reporterUserId(2L)
                .projectId(5L)
                .minutesTracked(10)
                .parentTaskId(2L)
                .linkedTasksIds(new ArrayList<>(List.of(3L)))
                .stageId(8L)
                .build();

        TeamMember teamMember = TeamMember.builder()
                .id(taskUpdater)
                .build();

        Project project = Project.builder()
                .id(taskDto.getProjectId())
                .build();

        Task taskToSave = Task.builder()
                .id(1L)
                .name("name")
                .description("description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .reporterUserId(2L)
                .project(project)
                .minutesTracked(10)
                .parentTask(parentTask)
                .linkedTasks(new ArrayList<>(List.of(linkedTask)))
                .stage(stage)
                .build();

        doNothing().when(taskValidator).validateTaskIdIsNotNull(taskDto.getId());
        when(teamMemberService.findById(teamMember.getId())).thenReturn(teamMember);
        when(projectService.getProjectById(project.getId())).thenReturn(project);
        doNothing().when(teamMemberValidator).
                validateIsTeamMemberParticipantOfProject(teamMember, project);

        when(taskRepository.findById(taskDto.getParentTaskId())).
                thenReturn(Optional.ofNullable(parentTask));
        when(taskRepository.findById(taskDto.getLinkedTasksIds().get(0))).
                thenReturn(Optional.of(linkedTask));
        when(stageService.getStageEntity(taskDto.getStageId())).thenReturn(stage);

        taskService.updateTask(taskDto, taskUpdater);

        verify(taskValidator, times(1)).validateTaskIdIsNotNull(taskDto.getId());
        verify(teamMemberService, times(1)).findById(teamMember.getId());
        verify(projectService).getProjectById(project.getId());
        verify(teamMemberValidator).
                validateIsTeamMemberParticipantOfProject(teamMember, project);
        verify(taskRepository, times(1)).findById(taskDto.getParentTaskId());
        verify(taskRepository, times(1)).findById(taskDto.getLinkedTasksIds().get(0));
        verify(stageService, times(1)).getStageEntity(taskDto.getStageId());
        verify(taskRepository).save(captor.capture());
        Task resultTask = captor.getValue();
        assertEquals(taskToSave, resultTask);
    }

    @Test
    public void getTaskThrowsExceptionTest() {
        long taskId = 10L;
        long requesterId = 10L;
        when(taskRepository.findById(taskId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> taskService.getTask(taskId, requesterId));
    }

    @Test
    public void getTaskTest() {
        long taskId = 1L;
        long projectId = 2L;
        long requesterId = 3L;

        Task parentTask = Task.builder()
                .id(10L)
                .build();

        Task linkedTask = Task.builder()
                .id(11L)
                .build();

        Stage stage = Stage.builder()
                .stageId(12L)
                .build();

        Project project = Project.builder()
                .id(projectId)
                .build();

        TeamMember teamMember = TeamMember.builder().build();

        Task task = Task.builder()
                .id(taskId)
                .name("name")
                .description("description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .reporterUserId(2L)
                .project(project)
                .minutesTracked(10)
                .parentTask(parentTask)
                .linkedTasks(new ArrayList<>(List.of(linkedTask)))
                .stage(stage)
                .build();

        TaskDto taskDto = TaskDto.builder()
                .id(taskId)
                .name("name")
                .description("description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .reporterUserId(2L)
                .projectId(project.getId())
                .minutesTracked(10)
                .parentTaskId(parentTask.getId())
                .linkedTasksIds(new ArrayList<>(List.of(linkedTask.getId())))
                .stageId(stage.getStageId())
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(teamMemberService.findById(requesterId)).thenReturn(teamMember);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        doNothing().when(teamMemberValidator).
                validateIsTeamMemberParticipantOfProject(teamMember, project);

        TaskDto result = taskService.getTask(taskId, requesterId);
        assertEquals(taskDto, result);
    }

    @Test
    public void getAllTasksTest() {
        long projectId = 2L;
        long requesterId = 3L;

        TaskFilterDto taskFilterDto = TaskFilterDto.builder().build();
        TeamMember teamMember = TeamMember.builder().build();

        Project project = Project.builder()
                .id(projectId)
                .build();

        Task parentTask = Task.builder()
                .id(10L)
                .build();

        Task linkedTask = Task.builder()
                .id(11L)
                .build();

        Stage stage = Stage.builder()
                .stageId(12L)
                .build();

        Task firstTask = Task.builder()
                .id(1L)
                .name("name")
                .description("description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .reporterUserId(2L)
                .project(project)
                .minutesTracked(10)
                .parentTask(parentTask)
                .linkedTasks(new ArrayList<>(List.of(linkedTask)))
                .stage(stage)
                .build();

        Task secondTask = Task.builder().build();
        List<Task> tasks = new ArrayList<>(List.of(firstTask, secondTask));

        TaskDto firstTaskDto = TaskDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .reporterUserId(2L)
                .projectId(project.getId())
                .minutesTracked(10)
                .parentTaskId(parentTask.getId())
                .linkedTasksIds(new ArrayList<>(List.of(linkedTask.getId())))
                .stageId(stage.getStageId())
                .build();

        TaskDto secondTaskDto = TaskDto.builder().build();
        List<TaskDto> listTaskDto = new ArrayList<>(List.of(firstTaskDto, secondTaskDto));

        when(teamMemberService.findById(requesterId)).thenReturn(teamMember);
        when(projectService.getProjectById(projectId)).thenReturn(project);
        doNothing().when(teamMemberValidator).
                validateIsTeamMemberParticipantOfProject(teamMember, project);
        when(taskRepository.findAllByProjectId(projectId)).
                thenReturn(tasks);
        when(filter.isApplicable(taskFilterDto)).thenReturn(true);
        when(filter.apply(any(), eq(taskFilterDto))).thenReturn(tasks.stream());

        List<TaskDto> result = taskService.getAllTasks(taskFilterDto, requesterId, projectId);
        assertEquals(listTaskDto, result);

        verify(teamMemberService, times(1)).findById(requesterId);
        verify(projectService, times(1)).getProjectById(projectId);
        verify(teamMemberValidator, times(1)).
                validateIsTeamMemberParticipantOfProject(teamMember, project);
        verify(taskRepository, times(1)).findAllByProjectId(projectId);
        verify(filter, times(1)).apply(any(), eq(taskFilterDto));
        verify(filter, times(1)).apply(any(), eq(taskFilterDto));
    }
}