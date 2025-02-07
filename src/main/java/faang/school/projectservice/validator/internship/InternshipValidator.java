package faang.school.projectservice.validator.internship;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternshipValidator {

    private static final int MAX_INTERNSHIP_MONTHS_DURATION = 3;

    public void validateMentorRoles(TeamMember mentor) {
        if (mentor.getRoles().contains(TeamRole.INTERN)) {
            throw new DataValidationException("The mentor can't be intern.");
        }
    }

    public void validateInternshipStarted(Internship internship) {
        boolean isNotStartedStatus = internship.getStatus().equals(InternshipStatus.NOT_STARTED);
        boolean isNotStartedDate = LocalDateTime.now().isAfter(internship.getStartDate());

        if (isNotStartedStatus && isNotStartedDate) {
            throw new DataValidationException(
                    "The internship with ID (%d) has not started yet!".formatted(internship.getId())
            );
        }
    }

    public void validateInternshipIncomplete(Internship internship) {
        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            throw new DataValidationException(
                    "The internship with ID (%d) has been already completed!".formatted(internship.getId())
            );
        }
    }

    public void validateNotExistingUserIds(List<Long> notExistingUserIds) {
        if (!notExistingUserIds.isEmpty()) {
            throw new DataValidationException(
                    "Not all user ids exist in database! Missing IDs: %s".formatted(notExistingUserIds)
            );
        }
    }

    public void validateInternshipDuration(LocalDateTime startDate, LocalDateTime endDate) {
        Period internshipDuration = Period.between(startDate.toLocalDate(), endDate.toLocalDate());
        int monthsDuration = internshipDuration.getMonths();
        int daysDuration = internshipDuration.getDays();

        boolean isMonthsDurationExceeded = monthsDuration > MAX_INTERNSHIP_MONTHS_DURATION;
        boolean isDaysDurationExceeded = internshipDuration.getMonths() == MAX_INTERNSHIP_MONTHS_DURATION &&  daysDuration > 0;

        if (isMonthsDurationExceeded || isDaysDurationExceeded) {
            throw new DataValidationException(
                    "The internship should last no more than %d months!".formatted(MAX_INTERNSHIP_MONTHS_DURATION)
            );
        }
    }

    public void validateExistingInterns(long internshipId, List<TeamMember> interns, List<Long> internUserIdsToCheck) {
        Set<Long> existingInternUserIds = interns.stream()
                .map(TeamMember::getUserId)
                .collect(Collectors.toSet());

        List<Long> invalidInternUserIds = internUserIdsToCheck.stream()
                .filter(internUserId -> !existingInternUserIds.contains(internUserId))
                .toList();

        if (!invalidInternUserIds.isEmpty()) {
            throw new DataValidationException("Some user IDs do not match any interns in internship with ID %d: %s"
                    .formatted(internshipId, invalidInternUserIds));
        }
    }
}
