package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VacancyValidator {

    private final TeamMemberService teamMemberService;

    public void isTheUserAManager(Long curatorId) {
        if (teamMemberService.findById(curatorId).getRoles().contains(TeamRole.MANAGER)) {
            log.info("User with ID {} is a manager", curatorId);
        } else {
            throw new DataValidationException("The user with ID " + curatorId + " is not a manager");
        }
    }

    public void validateVacancyStatus(VacancyStatus status, Vacancy vacancy) {
        log.info("Validating vacancy status: {}", status);
        if (status.equals(VacancyStatus.CLOSED) && vacancy.getCount() > vacancy.getCandidates().size()) {
            throw new DataValidationException("The required number of candidates was not recruited");
        }
    }
}
