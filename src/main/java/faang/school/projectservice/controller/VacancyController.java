package faang.school.projectservice.controller;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.VacancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Vacancy Management", description = "Operations related to managing vacancies"  )
@RestController
@RequestMapping("/vacancies")
@RequiredArgsConstructor()
public class VacancyController {

    private final VacancyService vacancyService;

    @Operation(summary = "Create a new vacancy")
    @PostMapping
    public ResponseEntity<Void> createVacancy(@RequestBody @Valid VacancyDto dto,
                                              @RequestHeader("x-user-id") @Positive Long userId) {
        vacancyService.createVacancy(dto, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a vacancy")
    @PutMapping("/{vacancyId}")
    public ResponseEntity<Void> updateVacancy(@PathVariable Long vacancyId,
                                              @RequestBody VacancyDto vacancyDto) {
        vacancyService.updateVacancy(vacancyId, vacancyDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a vacancy")
    @DeleteMapping("/{vacancyId}")
    public ResponseEntity<Void> deleteVacancy(@PathVariable Long vacancyId) {
        vacancyService.deleteVacancy(vacancyId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get vacancy by ID")
    @GetMapping("/{vacancyId}")
    public ResponseEntity<VacancyDto> getVacancyById(@PathVariable Long vacancyId) {
        return ResponseEntity.ok(vacancyService.getVacancyById(vacancyId));
    }

    @Operation(summary = "Get vacacies with given filters")
    @PostMapping("/filter")
    public ResponseEntity<Iterable<VacancyDto>> getVacanciesByFilter(@RequestBody VacancyFilterDto filter) {
        return ResponseEntity.ok(vacancyService.getVacanciesByFilter(filter));
    }
}
