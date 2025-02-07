package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.vacancy.VacancyNameFilter;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.CandidateService;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @Mock
    private CandidateService candidateService;

    @Mock
    private TeamMemberService teamMemberService;

    @InjectMocks
    private VacancyService vacancyService;

    @BeforeEach
    public void setUp() {
        List<Filter<Vacancy, VacancyFilterDto>> vacancyFilters = List.of(new VacancyNameFilter());
        ReflectionTestUtils.setField(vacancyService, "vacancyFilters", vacancyFilters);
    }

    @Test
    public void deleteVacancyTest() {
        Long id = 3L;
        Vacancy vacancy = new Vacancy();
        Candidate firstCandidate = new Candidate();
        firstCandidate.setId(1L);
        Candidate secondCandidate = new Candidate();
        secondCandidate.setId(2L);
        vacancy.setCandidates(List.of(firstCandidate, secondCandidate));
        when(vacancyRepository.findById(id)).thenReturn(Optional.of(vacancy));

        vacancyService.deleteVacancy(id);

        verify(vacancyRepository, times(1)).deleteById(id);
    }

    @Test
    public void getVacanciesByFilterTest() {
        VacancyFilterDto nameFilter = new VacancyFilterDto("First");
        Vacancy firstVacancy = new Vacancy();
        firstVacancy.setId(1L);
        firstVacancy.setName("First vacancy");
        Vacancy secondVacancy = new Vacancy();
        secondVacancy.setId(2L);
        secondVacancy.setName("Second vacancy");
        when(vacancyRepository.findAll()).thenReturn(List.of(firstVacancy, secondVacancy));

        List<VacancyDto> vacanciesByFilter = vacancyService.getVacanciesByFilter(nameFilter);

        assertEquals(1, vacanciesByFilter.size());
        assertEquals(firstVacancy.getName(), vacanciesByFilter.get(0).getName());
    }

    @Test
    public void findByIdTest() {
        Long id = 3L;
        Vacancy vacancy = new Vacancy();
        when(vacancyRepository.findById(id)).thenReturn(Optional.of(vacancy));

        vacancyService.findById(id);
    }
}