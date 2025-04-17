package faang.school.projectservice.filter.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class StageInvitationProjectNameFilterTest {

    StageInvitationProjectNameFilter projectNameFilter;
    StageInvitationFilterDto filterDto;

    @BeforeEach
    void setUp() {
        projectNameFilter = new StageInvitationProjectNameFilter();
        filterDto = new StageInvitationFilterDto();
    }

    @Test
    public void projectNamePatternIsApplicableTest() {
        filterDto.setProjectNamePattern("project");

        boolean result = projectNameFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void filterDescriptionPatternIsNullTest() {
        filterDto.setProjectNamePattern(null);

        boolean result = projectNameFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void filterIsNullTest() {
        filterDto = null;

        boolean result = projectNameFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void filterDescriptionPatternIsBlankTest() {
        filterDto.setProjectNamePattern("");

        boolean result = projectNameFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void applyTest() {
        Project project = new Project();
        project.setName("project");
        Project anotherProject = new Project();
        anotherProject.setName("another");

        Stage stage = new Stage();
        stage.setProject(project);
        Stage anotherStage = new Stage();
        anotherStage.setProject(anotherProject);

        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setStage(stage);
        StageInvitation anotherStageInvitation = new StageInvitation();
        anotherStageInvitation.setStage(anotherStage);

        Stream<StageInvitation> invitations = Stream.of(stageInvitation, anotherStageInvitation);
        filterDto.setProjectNamePattern("project");

        Stream<StageInvitation> result = projectNameFilter.apply(invitations, filterDto);

        assertEquals(1, result.count());
    }
}