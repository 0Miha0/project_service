package faang.school.projectservice.validator.stage_invitation;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class StageInvitationValidatorTest {

    private final StageInvitationValidator stageInvitationValidator = new StageInvitationValidator();

    @Test
    public void isInvitationSendToThisTeamMemberTest() {
        long teamMemberId = 1L;

        TeamMember teamMember = new TeamMember();
        teamMember.setId(teamMemberId);

        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setInvited(teamMember);

        assertDoesNotThrow(
                () -> stageInvitationValidator.validateIsInvitationSentToThisTeamMember(
                        teamMemberId, stageInvitation));
    }

    @Test
    public void isInvitationSendToThisTeamMemberThrowsExceptionTest() {
        long teamMemberId = 1L;
        long secondTeamMemberId = 101L;

        TeamMember teamMember = new TeamMember();
        teamMember.setId(teamMemberId);

        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setInvited(teamMember);

        assertThrows(DataValidationException.class,
                () -> stageInvitationValidator.validateIsInvitationSentToThisTeamMember(
                        secondTeamMemberId, stageInvitation));
    }
}