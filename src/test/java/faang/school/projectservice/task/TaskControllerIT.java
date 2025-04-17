package faang.school.projectservice.task;

import faang.school.projectservice.ProjectServiceApplicationTests;
import faang.school.projectservice.dto.task.CreateUpdateTaskDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
@Sql(scripts = "/task-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TaskControllerIT extends ProjectServiceApplicationTests {

    @Autowired
    private TaskController taskController;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void connectionEstablished() {
        assertThat(POSTGRESQL_CONTAINER.isCreated()).isTrue();
        assertThat(POSTGRESQL_CONTAINER.isRunning()).isTrue();
    }

    @Test
    void getTask() {
        long taskId = 1L;
        long requesterId = 1L;

        TaskDto result = taskController.getTask(taskId, requesterId);

        assertNotNull(result);
    }

    @Test
    void getAllTasksTest() {
        long projectId = 1L;
        long requesterId = 1L;
        TaskFilterDto taskFilterDto = TaskFilterDto.builder().build();

        List<TaskDto> result = taskController.getAllTasks(taskFilterDto, projectId, requesterId);

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    public void createTaskTest() {
        long creatorId = 1L;
        CreateUpdateTaskDto taskDto = CreateUpdateTaskDto.builder()
                .name("task to create")
                .description("description")
                .status(TaskStatus.TODO)
                .performerUserId(1L)
                .reporterUserId(2L)
                .projectId(1L)
                .minutesTracked(10)
                .parentTaskId(2L)
                .linkedTasksIds(new ArrayList<>(List.of(1L)))
                .stageId(1L)
                .build();

        TaskDto result = taskController.createTask(taskDto, creatorId);
        assert result != null;
        Task task = taskRepository.findById(result.getId()).orElseThrow(EntityNotFoundException::new);
        assertEquals(taskDto.getName(), task.getName());
        assertEquals(taskDto.getDescription(), task.getDescription());
        assertEquals(taskDto.getStatus(), task.getStatus());
        assertEquals(taskDto.getParentTaskId(), task.getParentTask().getId());
        assertEquals(taskDto.getProjectId(), task.getProject().getId());
        assertEquals(taskDto.getStageId(), task.getStage().getStageId());
    }

    @Test
    public void updateTaskTest() {
        long updaterId = 1L;
        CreateUpdateTaskDto taskDto = CreateUpdateTaskDto.builder()
                .id(2L)
                .name("task to update")
                .description("another description")
                .status(TaskStatus.DONE)
                .performerUserId(2L)
                .reporterUserId(1L)
                .projectId(1L)
                .minutesTracked(10)
                .parentTaskId(2L)
                .linkedTasksIds(new ArrayList<>())
                .stageId(1L)
                .build();

        taskController.updateTask(taskDto, updaterId);

        Task task = taskRepository.findById(2L).orElseThrow(EntityNotFoundException::new);
        assertEquals(taskDto.getName(), task.getName());
        assertEquals(taskDto.getDescription(), task.getDescription());
        assertEquals(taskDto.getStatus(), task.getStatus());
        assertEquals(taskDto.getParentTaskId(), task.getParentTask().getId());
        assertEquals(taskDto.getProjectId(), task.getProject().getId());
        assertEquals(taskDto.getStageId(), task.getStage().getStageId());
    }
}