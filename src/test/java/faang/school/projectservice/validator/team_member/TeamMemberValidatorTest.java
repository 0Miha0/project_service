package faang.school.projectservice.validator.team_member;

import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void teamMemberIsNotParticipantOfProjectThrowExceptionTest() {
        Long projectId = 10L;
        Long secondProjectId = 15L;

        Project project = new Project();
        project.setId(projectId);
        Project anotherProject = new Project();
        anotherProject.setId(secondProjectId);

        Stage stage = new Stage();
        stage.setProject(project);
        Stage anotherStage = new Stage();
        anotherStage.setProject(anotherProject);

        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setStage(stage);
        TeamMember teamMember = new TeamMember();
        teamMember.setStages(new ArrayList<>(List.of(anotherStage)));

        assertThrows(ForbiddenException.class,
                () -> teamMemberValidator.validateIsTeamMemberParticipantOfProject(
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

    @Test
    public void isTeamMemberParticipantOfProjectThrowsExceptionTest() {
        TeamMember teamMember = TeamMember.builder().build();
        Team team = Team.builder()
                .teamMembers(new ArrayList<>())
                .build();
        Project project = Project.builder()
                .teams(new ArrayList<>(List.of(team)))
                .build();

        assertThrows(ForbiddenException.class,
                () -> teamMemberValidator.validateIsTeamMemberParticipantOfProject(
                        teamMember, project));
    }
}