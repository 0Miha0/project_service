package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StageTaskStatusFilterTest {

    private StageTaskStatusFilter stageTaskStatusFilter;
    private StageFilterDto stageFilterDto;

    @BeforeEach
    public void setUp() {
        stageTaskStatusFilter = new StageTaskStatusFilter();
        stageFilterDto = new StageFilterDto();
    }

    @Test
    @DisplayName("Is applicable successful filter test")
    public void isApplicableFilterTest() {
        stageFilterDto.setTaskStatusPattern("completed");

        assertTrue(stageTaskStatusFilter.isApplicable(stageFilterDto));
    }

    @Test
    @DisplayName("Filter is null test")
    public void filterNullTest() {
        assertFalse(stageTaskStatusFilter.isApplicable(null));
    }

    @Test
    @DisplayName("Filter is not blunk test")
    public void filterNotBlunkTest() {
        stageFilterDto.setTaskStatusPattern("");

        assertFalse(stageTaskStatusFilter.isApplicable(stageFilterDto));
    }

    @Test
    @DisplayName("apply successful test")
    public void applySuccessfulTest() {
        Stage stage1 = Stage.builder()
                .tasks(List.of(
                        Task.builder().status(TaskStatus.TODO).build()))
                .build();

        Stage stage2 = Stage.builder()
                .tasks(List.of(
                        Task.builder().status(TaskStatus.TODO).build()))
                .build();

        Stage stage3 = Stage.builder()
                .tasks(List.of(
                        Task.builder().status(TaskStatus.CANCELLED).build()))
                .build();

        stageFilterDto.setTaskStatusPattern("todo");
        Stream<Stage> stages = Stream.of(stage1, stage2, stage3);

        List<Stage> filteredStages = stageTaskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(2, filteredStages.size());
        assertTrue(filteredStages.contains(stage1));
        assertTrue(filteredStages.contains(stage2));
        assertFalse(filteredStages.contains(stage3));
    }
}
