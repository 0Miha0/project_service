package faang.school.projectservice.filter.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
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
class StageInvitationDescriptionFilterTest {

    StageInvitationDescriptionFilter descriptionFilter;
    StageInvitationFilterDto filterDto;

    @BeforeEach
    void setUp() {
        descriptionFilter = new StageInvitationDescriptionFilter();
        filterDto = new StageInvitationFilterDto();
    }

    @Test
    public void filterDescriptionPatternIsApplicableTest() {
        filterDto.setDescriptionPattern("description");

        boolean result = descriptionFilter.isApplicable(filterDto);

        assertTrue(result);
    }

    @Test
    public void filterIsNullTest() {
        filterDto = null;

        boolean result = descriptionFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void filterDescriptionPatternIsNullTest() {
        filterDto.setDescriptionPattern(null);

        boolean result = descriptionFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void filterDescriptionPatternIsBlankTest() {
        filterDto.setDescriptionPattern("");

        boolean result = descriptionFilter.isApplicable(filterDto);

        assertFalse(result);
    }

    @Test
    public void applyTest() {
        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setDescription("description");

        StageInvitation anotherStageInvitation = new StageInvitation();
        anotherStageInvitation.setDescription("another");

        Stream<StageInvitation> invitations = Stream.of(stageInvitation, anotherStageInvitation);
        filterDto.setDescriptionPattern("description");

        Stream<StageInvitation> result = descriptionFilter.apply(invitations, filterDto);

        assertEquals(1, result.count());
    }
}