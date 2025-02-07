package faang.school.projectservice.validation.team_member;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.validator.team_member.TeamMemberValidator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TeamMemberValidatorTest {

    private final TeamMemberValidator teamMemberValidator = new TeamMemberValidator();

    @Test
    public void teamMemberParticipantOfProjectTest() {
        long projectId = 10L;

        Project project = new Project();
        project.setId(projectId);

        Stage stage = new Stage();
        stage.setProject(project);

        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setStage(stage);
        TeamMember teamMember = new TeamMember();
        teamMember.setStages(new ArrayList<>(List.of(stage)));

        teamMemberValidator.validateIsTeamMemberParticipantOfProject(teamMember, stageInvitation);

        assertDoesNotThrow(() -> teamMemberValidator.validateIsTeamMemberParticipantOfProject(
                teamMember, stageInvitation));
    }

    @Test
    public void isTeamMemberParticipantOfProjectTest() {
        TeamMember teamMember = TeamMember.builder().build();
        Team team = Team.builder()
                .teamMembers(new ArrayList<>(List.of(teamMember)))
                .build();
        Project project = Project.builder()
                .teams(new ArrayList<>(List.of(team)))
                .build();

        assertDoesNotThrow(() -> teamMemberValidator.validateIsTeamMemberParticipantOfProject(
                teamMember, project));
    }
}