package faang.school.projectservice.controller;

import faang.school.projectservice.dto.task.CreateUpdateTaskDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.service.task.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Tasks", description = "Operations related to tasks")
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid task data or request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access the project"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred during processing"),
    })
    @PostMapping()
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid CreateUpdateTaskDto taskDto,
                                              @RequestHeader("x-team-member-id") long creatorId) {
        return ResponseEntity.ok(taskService.createTask(taskDto, creatorId));
    }

    @Operation(summary = "Update a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid task data or request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access the project"),
            @ApiResponse(responseCode = "404", description = "Project or task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred during processing"),
    })
    @PutMapping()
    public ResponseEntity<TaskDto> updateTask(@RequestBody @Valid CreateUpdateTaskDto taskDto,
                              @RequestHeader("x-team-member-id") long updaterId) {
        return ResponseEntity.ok(taskService.updateTask(taskDto, updaterId));
    }

    @Operation(summary = "Get all tasks"  )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access the project"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred during processing"),
    })
    @PostMapping("/filters")
    public ResponseEntity<List<TaskDto>> getAllTasks(@RequestBody TaskFilterDto taskFilterDto,
                                     @RequestParam Long projectId,
                                     @RequestHeader("x-team-member-id") long requesterId) {
        return ResponseEntity.ok(taskService.getAllTasks(taskFilterDto, projectId, requesterId));
    }

    @Operation(summary = "Get a task by id"  )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized to access the project"),
            @ApiResponse(responseCode = "404", description = "Project or task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred during processing"),
    })
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTask(@PathVariable long taskId,
                           @RequestHeader("x-team-member-id") long requesterId) {
        return ResponseEntity.ok(taskService.getTask(taskId, requesterId));
    }
}
