package faang.school.projectservice.service;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    @Transactional
    public Team save(Team team) {
        log.info("Saving team: {}", team);
        return teamRepository.save(team);
    }

    public Team findById(Long id) {
        log.info("");
        return teamRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Team not found with id: " + id));
    }

    public List<Team> findAllById(List<Long> ids) {
        return teamRepository.findAllById(ids);
    }
}