package faang.school.projectservice.controller;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Moments"  , description = "Operations related to moments"  )
@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService service;

    @Operation(summary = "Create a new moment")
    @PostMapping()
    public ResponseEntity<MomentDto> createMoment(@RequestBody @Valid MomentDto momentDto) {
        return ResponseEntity.ok(service.createMoment(momentDto));
    }

    @Operation(summary = "Update an existing moment"  )
    @PutMapping("/{momentId}")
    public ResponseEntity<MomentDto> updateMoment(@PathVariable("momentId") Long momentId,
                                                  @RequestBody @Valid MomentDto updatedMomentDto) {
        return ResponseEntity.ok(service.updateMoment(updatedMomentDto, momentId));
    }

    @Operation(summary = "Get moments by filter"  )
    @PostMapping("/filter")
    public ResponseEntity<List<MomentDto>> getMomentsByFilter(@RequestBody @Valid MomentFilterDto momentFilterDto) {
        return ResponseEntity.ok(service.getMomentsByFilter(momentFilterDto));
    }

    @Operation(summary = "Get all moments"  )
    @GetMapping("/all")
    public ResponseEntity<List<MomentDto>> getAllMoments() {
        return ResponseEntity.ok(service.getAllMoments());
    }

    @Operation(summary = "Get a moment by ID"  )
    @GetMapping("/{momentId}")
    public ResponseEntity<MomentDto> getMomentById(@PathVariable("momentId") Long id) {
        return ResponseEntity.ok(service.getMomentById(id));
    }

}