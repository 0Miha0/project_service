package faang.school.projectservice.filter.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
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
class StageInvitationStageNameFilterTest {

    StageInvitationStageNameFilter stageNameFilter;
    StageInvitationFilterDto filterDto;

    @BeforeEach
    void setUp() {
        stageNameFilter = new StageInvitationStageNameFilter();
        filterDto = new StageInvitationFilterDto();
    }

    @Test
    public void projectNamePatternIsApplicableTest() {
        filterDto.setStageNamePattern("stage");

        boolean result = stageNameFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void filterDescriptionPatternIsNullTest() {
        filterDto.setStageNamePattern(null);

        boolean result = stageNameFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void filterIsNullTest() {
        filterDto = null;

        boolean result = stageNameFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void filterDescriptionPatternIsBlankTest() {
        filterDto.setStageNamePattern("");

        boolean result = stageNameFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void applyTest() {
        Stage stage = new Stage();
        stage.setStageName("stage");
        Stage anotherStage = new Stage();
        anotherStage.setStageName("another");

        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setStage(stage);
        StageInvitation anotherStageInvitation = new StageInvitation();
        anotherStageInvitation.setStage(anotherStage);

        Stream<StageInvitation> invitations = Stream.of(stageInvitation, anotherStageInvitation);
        filterDto.setStageNamePattern("stage");

        Stream<StageInvitation> result = stageNameFilter.apply(invitations, filterDto);

        assertEquals(1, result.toList().size());
    }
}