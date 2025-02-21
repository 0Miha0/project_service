package faang.school.projectservice.controller.project;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Project Management", description = "Operations related to managing projects")
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor()
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Create a new project",
            description = "Creates a new project with the provided details.")
    @PostMapping
    public ResponseEntity<Void> createProject(@RequestBody @Valid ProjectCreateDto dto,
                                              @RequestHeader("x-user-id") @Positive Long userId) {
        projectService.createProject(dto, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update project details",
            description = "Updates an existing project's details with the provided information.")
    @PutMapping
    public ResponseEntity<Void> updateProject(@RequestBody @Valid ProjectUpdateDto dto) {
        projectService.updateProject(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a project",
            description = "Soft deletes a project by setting its status to DELETED.")
    @PostMapping("/filter")
    public ResponseEntity<List<ProjectUpdateDto>> findAllProjects(@RequestBody @Valid ProjectFilterDto filters,
                                            @Parameter(description = "User ID of the requester. " +
                                                    "Must be a positive number.")
                                            @RequestHeader("x-user-id") @Positive Long userId) {
        return ResponseEntity.ok(projectService.findAllProjects(filters, userId));
    }

    @Operation(summary = "Get project details",
            description = "Fetches the project details based on the project ID.")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectUpdateDto> findProjectById(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.findProjectById(projectId));
    }
}
