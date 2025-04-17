package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StageTeamRoleFilterTest {

    private StageTeamRoleFilter stageTeamRoleFilter;
    private StageFilterDto stageFilterDto;

    @BeforeEach
    public void setUp() {
        stageTeamRoleFilter = new StageTeamRoleFilter();
        stageFilterDto = new StageFilterDto();
    }

    @Test
    @DisplayName("Is applicable successful filter test")
    public void isApplicableFilterTest() {
        stageFilterDto.setTeamRolePattern("owner");

        assertTrue(stageTeamRoleFilter.isApplicable(stageFilterDto));
    }

    @Test
    @DisplayName("Filter is null test")
    public void filterNullTest() {
        assertFalse(stageTeamRoleFilter.isApplicable(null));
    }

    @Test
    @DisplayName("Filter is not blunk test")
    public void filterNotBlunkTest() {
        stageFilterDto.setTaskStatusPattern("");

        assertFalse(stageTeamRoleFilter.isApplicable(stageFilterDto));
    }

    @Test
    @DisplayName("apply successful test")
    public void applySuccessfulTest() {
        Stage stage1 = Stage.builder()
                .stageRoles(List.of(
                        StageRoles.builder().teamRole(TeamRole.DESIGNER).build()))
                .build();

        Stage stage2 = Stage.builder()
                .stageRoles(List.of(
                        StageRoles.builder().teamRole(TeamRole.DESIGNER).build()))
                .build();

        Stage stage3 = Stage.builder()
                .stageRoles(List.of(
                        StageRoles.builder().teamRole(TeamRole.OWNER).build()))
                .build();

        stageFilterDto.setTeamRolePattern("designer");
        Stream<Stage> stages = Stream.of(stage1, stage2, stage3);

        List<Stage> filteredStages = stageTeamRoleFilter.apply(stages, stageFilterDto).toList();

        assertEquals(2, filteredStages.size());
        assertTrue(filteredStages.contains(stage1));
        assertTrue(filteredStages.contains(stage2));
        assertFalse(filteredStages.contains(stage3));
    }
}
