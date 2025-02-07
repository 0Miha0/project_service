package faang.school.projectservice.service.candidate;

import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.service.CandidateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private CandidateService candidateService;

    @Test
    public void deleteCandidateTest() {
        List<Long> id = List.of(1L, 2L, 3L);

        candidateService.deleteAllById(id);

        verify(candidateRepository, times(1)).deleteAllById(id);
    }
}