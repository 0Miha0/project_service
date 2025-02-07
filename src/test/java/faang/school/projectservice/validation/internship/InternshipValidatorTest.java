package faang.school.projectservice.validation.internship;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validator.internship.InternshipValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InternshipValidatorTest {

    private static final int MAX_INTERNSHIP_MONTHS_DURATION = 3;

    private InternshipValidator validator;

    @BeforeEach
    void setUp() {
        validator = new InternshipValidator();
    }

    @Test
    void validateMentorRolesExceptionTest() {
        TeamMember mentor = new TeamMember();
        mentor.setRoles(List.of(TeamRole.INTERN, TeamRole.ANALYST, TeamRole.MANAGER));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateMentorRoles(mentor));
        assertEquals("The mentor can't be intern.", exception.getMessage());
    }

    @Test
    void validateMentorRolesValidTest() {
        TeamMember mentor = new TeamMember();
        mentor.setRoles(List.of(TeamRole.ANALYST, TeamRole.MANAGER));

        assertDoesNotThrow(() -> validator.validateMentorRoles(mentor));
    }

    @Test
    void validateInternshipStartedValidTest() {
        long internshipId = 10L;
        Internship internship = new Internship();
        internship.setId(internshipId);
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        internship.setStartDate(LocalDateTime.now().plusMonths(1));

        assertDoesNotThrow(() ->  validator.validateInternshipStarted(internship));
    }

    @Test
    void validateInternshipStartedExceptionTest() {
        long internshipId = 10L;
        Internship internship = new Internship();
        internship.setId(internshipId);
        internship.setStatus(InternshipStatus.NOT_STARTED);
        internship.setStartDate(LocalDateTime.now().minusMonths(1));

        DataValidationException exception =
                assertThrows(DataValidationException.class, () ->  validator.validateInternshipStarted(internship));
        assertEquals("The internship with ID (%d) has not started yet!".formatted(internshipId), exception.getMessage());
    }

    @Test
    void validateInternshipIncompleteValidTest() {
        long internshipId = 10L;
        Internship internship = new Internship();
        internship.setId(internshipId);
        internship.setStatus(InternshipStatus.IN_PROGRESS);

        assertDoesNotThrow(() ->  validator.validateInternshipIncomplete(internship));
    }

    @Test
    void validateInternshipIncompleteExceptionTest() {
        long internshipId = 10L;
        Internship internship = new Internship();
        internship.setId(internshipId);
        internship.setStatus(InternshipStatus.COMPLETED);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () ->  validator.validateInternshipIncomplete(internship));
        assertEquals("The internship with ID (%d) has been already completed!".formatted(internshipId), exception.getMessage());
    }

    @Test
    void validateNotExistingUserIdsExceptionTest() {
        List<Long> notExistingUserIds = List.of(1L, 2L, 3L);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateNotExistingUserIds(notExistingUserIds));
        assertEquals("Not all user ids exist in database! Missing IDs: %s".formatted(notExistingUserIds), exception.getMessage());
    }

    @Test
    void validateNotExistingUserIdsValidTest() {
        List<Long> notExistingUserIds = List.of();
        assertDoesNotThrow(() -> validator.validateNotExistingUserIds(notExistingUserIds));
    }

    @Test
    void validateInternshipDurationExceptionTest() {
        LocalDateTime startDate = LocalDateTime.of(2025, 1, 25, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 4, 26, 0, 0);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validateInternshipDuration(startDate, endDate));
        assertEquals(
                "The internship should last no more than %d months!".formatted(MAX_INTERNSHIP_MONTHS_DURATION),
                exception.getMessage()
        );
    }

    @Test
    void validateExistingUserIdsExceptionTest() {
        long internshipId = 10L;
        long notExistingInternUserId = 4L;
        List<TeamMember> interns = List.of(
                TeamMember.builder().userId(1L).build(),
                TeamMember.builder().userId(2L).build(),
                TeamMember.builder().userId(3L).build()
        );
        List<Long> internUserIdsToCheck = List.of(1L, 2L, notExistingInternUserId);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> validator.validateExistingInterns(internshipId, interns, internUserIdsToCheck)
        );
        assertEquals(
                "Some user IDs do not match any interns in internship with ID %d: %s"
                        .formatted(internshipId, List.of(notExistingInternUserId)),
                exception.getMessage()
        );
    }
}