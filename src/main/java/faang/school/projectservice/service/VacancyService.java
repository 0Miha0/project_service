package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {

    private final CandidateService candidateService;
    private final VacancyRepository vacancyRepository;
    private final VacancyValidator vacancyValidator;
    private final VacancyMapper vacancyMapper;
    private final List<Filter<Vacancy, VacancyFilterDto>> vacancyFilters;

    public void createVacancy(VacancyDto dto, Long userId) {
        log.info("Creating vacancy with name: {}", dto.getName());
        vacancyValidator.isTheUserAManager(userId);
        Vacancy vacancy = vacancyMapper.toEntity(dto);
        vacancy.setCreatedAt(LocalDateTime.now());
        save(vacancy);
        log.info("Vacancy created successfully: {}", dto.getName());
    }

    public void updateVacancy(Long vacancyId, VacancyDto vacancyDto) {
        log.info("Updating vacancy with ID: {} using data: {}", vacancyId, vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancyValidator.validateVacancyStatus(vacancy.getStatus(), vacancy);
        save(vacancy);
        log.info("Update vacancy with ID {}", vacancyId);
    }

    public void deleteVacancy(Long id) {
        log.info("Deleting all candidates from vacancy with ID {}", id);
        Vacancy vacancy = findById(id);
        candidateService.deleteAllById(vacancy.getCandidates().stream()
                .map(Candidate::getId)
                .toList());
        log.info("Delete vacancy with ID {}", id);
        deleteById(id);
    }

    public VacancyDto getVacancyById(Long id) {
        log.info("Retrieving vacancy with ID: {}", id);
        return vacancyMapper.toDto(findById(id));
    }

    public List<VacancyDto> getVacanciesByFilter(VacancyFilterDto filters) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        return vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(vacancies, filters))
                .map(vacancyMapper::toDto)
                .toList();
    }

    public void deleteById(Long id) {
        log.info("Deleting vacancy with ID: {}", id);
        vacancyRepository.deleteById(id);
        log.info("Vacancy deleted successfully: {}", id);
    }

    public Vacancy findById(Long vacancyId) {
        log.info("Retrieving vacancy by ID: {}", vacancyId);
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy not found for ID: " + vacancyId));
    }

    public void save(Vacancy vacancy) {
        log.info("Saving vacancy: {}", vacancy.getName());
        vacancyRepository.save(vacancy);
        log.info("Vacancy saved successfully: {}", vacancy.getName());
    }

}
