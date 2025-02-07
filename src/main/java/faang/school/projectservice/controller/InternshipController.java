package faang.school.projectservice.controller;

import faang.school.projectservice.dto.internship.InternshipCreationDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.dto.internship.InternshipUpdateRequestDto;
import faang.school.projectservice.service.InternshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Internship", description = "Operations related to internships"  )
@RestController
@RequestMapping("/internships")
@RequiredArgsConstructor
public class InternshipController {

    private final InternshipService internshipService;

    @Operation(summary = "Create a new internship")
    public ResponseEntity<InternshipDto> createInternship(@RequestBody @Valid InternshipCreationDto internshipCreationDto) {
        InternshipDto internshipDto = internshipService.createInternship(internshipCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(internshipDto);
    }

    @PatchMapping("/{internshipId}")
    public ResponseEntity<InternshipUpdateRequestDto> updateInternship(
            @PathVariable Long internshipId,
            @RequestBody @Valid InternshipUpdateDto updateDto
    ) {
        InternshipUpdateRequestDto internshipDto = internshipService.updateInternship(internshipId, updateDto);
        return ResponseEntity.ok(internshipDto);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<InternshipDto>> getFilteredInternships(@RequestBody InternshipFilterDto filterDto) {
        List<InternshipDto> filteredInternships = internshipService.getFilteredInternships(filterDto);
        return ResponseEntity.ok(filteredInternships);
    }

    @GetMapping
    public ResponseEntity<List<InternshipDto>> getAllInternships() {
        List<InternshipDto> filteredInternships = internshipService.getAllInternships();
        return ResponseEntity.ok(filteredInternships);
    }

    @GetMapping("/{internshipId}")
    public ResponseEntity<InternshipDto> getInternshipById(@PathVariable Long internshipId) {
        InternshipDto internship = internshipService.getInternshipById(internshipId);
        return ResponseEntity.ok(internship);
    }

    @DeleteMapping("/{internshipId}/interns")
    public ResponseEntity<Void> deleteInternsFromInternship(
            @PathVariable Long internshipId,
            @RequestBody List<Long> internUserIdsToRemove) {
        internshipService.removeInternsFromInternship(internshipId, internUserIdsToRemove);
        return ResponseEntity.noContent().build();
    }
}
