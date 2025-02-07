package faang.school.projectservice.validator.team_member;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TeamMemberValidator {

    public void validateIsTeamMemberParticipantOfProject(TeamMember teamMember, StageInvitation invitation) {
        Project invitationProject = invitation.getStage().getProject();

        boolean isNotParticipant = teamMember.getStages().stream()
                .noneMatch(stage -> stage.getProject().getId().equals(invitationProject.getId()));

        if (isNotParticipant) {
            throw new DataValidationException("This team member is not participant of this project");
        }
    }

    public void validateIsTeamMemberParticipantOfProject(TeamMember teamMember, Project project) {
        if (project.getTeams().stream()
                .noneMatch(team -> team.getTeamMembers().contains(teamMember))) {
            throw new DataValidationException("This team member is not participant of this project");
        }
    }

    public <T extends Number> void validationOnNullLessThanOrEqualToZero(T value, String message) {
        if (value == null || value.doubleValue() <= 0) {
            log.error(message);
            throw new DataValidationException(message);
        }
    }
}
