package faang.school.projectservice.service;

import faang.school.projectservice.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public void deleteAllById(List<Long> ids) {
        candidateRepository.deleteAllById(ids);
        log.info("Delete candidate with IDs {}", ids);
    }
}
