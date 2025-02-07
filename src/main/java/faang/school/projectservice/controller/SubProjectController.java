package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.service.SubProjectService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/sub-projects")
@RequiredArgsConstructor
public class SubProjectController {

    private final SubProjectService subProjectService;

    @GetMapping
    public ResponseEntity<List<ProjectDto>> findSubProjects(@PathVariable Long projectId,
                                                            @RequestBody SubProjectFilterDto filters,
                                                            @RequestHeader("x-user-id") Long userId) {
        return ResponseEntity.ok(subProjectService.findSubProjects(projectId, filters, userId));
    }

    @PostMapping
    public ResponseEntity<Void> createSubProject(@PathVariable("projectId") Long projectId,
                                                 @Valid @RequestBody CreateSubProjectDto dto,
                                                 @RequestHeader("x-user-id") Long userId) {
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(subProjectService.createSubProject(projectId, dto, userId).getId())
                        .toUri()
        ).build();
    }

    @PutMapping("/{subProjectId}")
    public ResponseEntity<Void> updateSubProject(@PathVariable Long projectId,
                                                 @PathVariable Long subProjectId,
                                                 @Valid @RequestBody ProjectDto dto,
                                                 @RequestHeader("x-user-id") Long userId) {
        subProjectService.updateSubProject(projectId, subProjectId, dto, userId);
        return ResponseEntity.noContent().build();
    }
}
