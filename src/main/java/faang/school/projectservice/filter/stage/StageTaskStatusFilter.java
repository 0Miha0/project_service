package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.stage.Stage;
import io.micrometer.common.util.StringUtils;

import java.util.stream.Stream;

public class StageTaskStatusFilter implements Filter<Stage, StageFilterDto> {

    @Override
    public boolean isApplicable(StageFilterDto stageFilterDto) {
        return stageFilterDto != null && StringUtils.isNotBlank(stageFilterDto.getTaskStatusPattern());
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stages, StageFilterDto stageFilterDto) {
        String taskStatusPattern = stageFilterDto.getTaskStatusPattern().toLowerCase();
        return stages.filter(stage ->
                stage.getTasks().stream()
                        .anyMatch(task ->
                                task.getStatus().name().toLowerCase().contains(taskStatusPattern)));
    }
}
