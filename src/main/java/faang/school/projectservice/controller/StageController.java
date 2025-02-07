package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageDeleteDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.service.StageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Stages"  , description = "Operations related to stages"  )
@RestController
@RequestMapping("/stages")
@RequiredArgsConstructor()
public class StageController {

    private final StageService stageService;

    @Operation(summary = "Create a new stage")
    @PostMapping
    public ResponseEntity<StageDto> createStage(@RequestBody @Valid StageDto stageDto) {
        return ResponseEntity.ok(stageService.createStage(stageDto));
    }

    @Operation(summary = "Get all stages")
    @PostMapping("/filter")
    public ResponseEntity<List<StageDto>> getStageWithFilter(@RequestBody StageFilterDto stageFilterDto) {
        return ResponseEntity.ok(stageService.getStageByFilter(stageFilterDto));
    }

    @Operation(summary = "Delete a stage")
    @DeleteMapping()
    public ResponseEntity<Void> deleteStage(@RequestBody StageDeleteDto stageDeleteDto) {
        stageService.deleteStage(stageDeleteDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a stage")
    @PutMapping
    public ResponseEntity<StageDto> updateStage(@RequestBody StageDto stageDto) {
        return ResponseEntity.ok(stageService.updateStage(stageDto));
    }

    @Operation(summary = "Get all stages for a project")
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<StageDto>> getAllStage(@PathVariable Long projectId) {
        return ResponseEntity.ok(stageService.getAllProjectStages(projectId));
    }

    @Operation(summary = "Get a stage by ID")
    @GetMapping("/{id}")
    public ResponseEntity<StageDto> getStageById(@PathVariable Long id) {
        return ResponseEntity.ok(stageService.getStageById(id));
    }
}
