package faang.school.projectservice.controller.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.controller.TaskController;
import faang.school.projectservice.dto.task.CreateUpdateTaskDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.task.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(TaskController.class)
//@Import(UserContext.class)
//@AutoConfigureMockMvc
//public class TaskControllerMockMvcTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private TaskService taskService;
//
//    private CreateUpdateTaskDto createTaskDto;
//    private TaskDto taskDto;
//    private String taskUrl;
//
//    @BeforeEach
//    void setUp() {
//        createTaskDto = CreateUpdateTaskDto.builder()
//                .name("task to create")
//                .description("description")
//                .status(TaskStatus.TODO)
//                .performerUserId(1L)
//                .reporterUserId(2L)
//                .projectId(1L)
//                .minutesTracked(10)
//                .parentTaskId(2L)
//                .linkedTasksIds(new ArrayList<>(List.of(1L)))
//                .stageId(1L)
//                .build();
//
//        taskDto = TaskDto.builder()
//                .id(1L)
//                .name("task to create")
//                .description("description")
//                .status(TaskStatus.TODO)
//                .performerUserId(1L)
//                .reporterUserId(2L)
//                .minutesTracked(10)
//                .parentTaskId(2L)
//                .linkedTasksIds(new ArrayList<>(List.of(1L)))
//                .projectId(1L)
//                .stageId(1L)
//                .build();
//
//        taskUrl = "/tasks";
//    }
//
//    @Test
//    void createTaskClientErrorTest() throws Exception {
//        mockMvc.perform(
//                post(taskUrl)
//                        .header("x-team-member-id", 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}")
//        ).andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    void createTaskTest() throws Exception {
//        long creatorId = 1L;
//
//        when(taskService.createTask(any(CreateUpdateTaskDto.class), anyLong())).thenReturn(taskDto);
//
//        mockMvc.perform(
//                        post(taskUrl)
//                                .header("x-team-member-id", creatorId)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .characterEncoding(StandardCharsets.UTF_8)
//                                .content(objectMapper.writeValueAsString(createTaskDto))
//                ).andExpect(status().isOk())
//                .andExpect(this::assertJsonResponse);
//    }
//
//    @Test
//    void updateTaskClientErrorTest() throws Exception {
//        mockMvc.perform(
//                put(taskUrl)
//                        .header("x-team-member-id", 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}")
//        ).andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    void updateTaskTest() throws Exception {
//        long updaterId = 1L;
//
//        when(taskService.updateTask(any(CreateUpdateTaskDto.class), anyLong())).thenReturn(taskDto);
//
//        mockMvc.perform(
//                        put(taskUrl)
//                                .header("x-team-member-id", updaterId)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .characterEncoding(StandardCharsets.UTF_8)
//                                .content(objectMapper.writeValueAsString(createTaskDto))
//                ).andExpect(status().isOk())
//                .andExpect(this::assertJsonResponse);
//    }
//
//    @Test
//    void getTaskNotFoundExceptionTest() throws Exception {
//        long taskId = 1L;
//        long requesterId = 1L;
//
//        when(taskService.getTask(taskId, requesterId)).
//                thenThrow(new EntityNotFoundException("Task not found"));
//
//        mockMvc.perform(
//                get("/tasks{taskId}", taskId)
//                        .header("x-team-member-id", 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//        ).andExpect(status().isNotFound());
//    }
//
//    @Test
//    void getTaskTest() throws Exception {
//        long taskId = 1L;
//        long requesterId = 1L;
//
//        when(taskService.getTask(taskId, requesterId)).thenReturn(taskDto);
//
//        mockMvc.perform(
//                        get("/tasks/{taskId}", taskId)
//                                .header("x-team-member-id", requesterId)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .characterEncoding(StandardCharsets.UTF_8)
//                ).andExpect(status().isOk())
//                .andExpect(this::assertJsonResponse);
//    }
//
//    @Test
//    void getAllTasksTest() throws Exception {
//        TaskFilterDto taskFilterDto = TaskFilterDto.builder().build();
//        long projectId = 1L;
//        long requesterId = 1L;
//
//        when(taskService.getAllTasks(any(TaskFilterDto.class), eq(projectId), eq(requesterId)))
//                .thenReturn(List.of(taskDto));
//
//        mockMvc.perform(
//                        post("/tasks/filters")
//                                .header("x-team-member-id", requesterId)
//                                .param("projectId", String.valueOf(projectId))
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(taskFilterDto))
//                )
//                .andExpect(status().isOk())
//                .andExpect(this::assertJsonArrayResponse);
//    }
//
//    private void assertJsonResponse(MvcResult result) throws Exception {
//        MockMvcResultMatchers.jsonPath("$.id").value(taskDto.getId()).match(result);
//        MockMvcResultMatchers.jsonPath("$.name").value(taskDto.getName()).match(result);
//        MockMvcResultMatchers.jsonPath("$.description").value(taskDto.getDescription()).match(result);
//        MockMvcResultMatchers.jsonPath("$.status").value(taskDto.getStatus().toString()).match(result);
//        MockMvcResultMatchers.jsonPath("$.performerUserId").value(taskDto.getPerformerUserId()).match(result);
//        MockMvcResultMatchers.jsonPath("$.reporterUserId").value(taskDto.getReporterUserId()).match(result);
//        MockMvcResultMatchers.jsonPath("$.minutesTracked").value(taskDto.getMinutesTracked()).match(result);
//        MockMvcResultMatchers.jsonPath("$.parentTaskId").value(taskDto.getParentTaskId()).match(result);
//        MockMvcResultMatchers.jsonPath("$.linkedTasksIds[0]").value(taskDto.getLinkedTasksIds().get(0)).match(result);
//        MockMvcResultMatchers.jsonPath("$.projectId").value(taskDto.getProjectId()).match(result);
//        MockMvcResultMatchers.jsonPath("$.stageId").value(taskDto.getStageId()).match(result);
//    }
//
//    private void assertJsonArrayResponse(MvcResult result) throws Exception {
//        MockMvcResultMatchers.jsonPath("$[0].id").value(taskDto.getId()).match(result);
//        MockMvcResultMatchers.jsonPath("$[0].name").value(taskDto.getName()).match(result);
//        MockMvcResultMatchers.jsonPath("$[0].description").value(taskDto.getDescription()).match(result);
//        MockMvcResultMatchers.jsonPath("$[0].status").value(taskDto.getStatus().toString()).match(result);
//        MockMvcResultMatchers.jsonPath("$[0].performerUserId").value(taskDto.getPerformerUserId()).match(result);
//        MockMvcResultMatchers.jsonPath("$[0].reporterUserId").value(taskDto.getReporterUserId()).match(result);
//        MockMvcResultMatchers.jsonPath("$[0].minutesTracked").value(taskDto.getMinutesTracked()).match(result);
//        MockMvcResultMatchers.jsonPath("$[0].parentTaskId").value(taskDto.getParentTaskId()).match(result);
//        MockMvcResultMatchers.jsonPath("$[0].linkedTasksIds[0]").value(taskDto.getLinkedTasksIds().get(0)).match(result);
//        MockMvcResultMatchers.jsonPath("$[0].projectId").value(taskDto.getProjectId()).match(result);
//        MockMvcResultMatchers.jsonPath("$[0].stageId").value(taskDto.getStageId()).match(result);
//    }
//}