package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.stage.Stage;
import io.micrometer.common.util.StringUtils;

import java.util.stream.Stream;

public class StageTeamRoleFilter implements Filter<Stage, StageFilterDto> {

    @Override
    public boolean isApplicable(StageFilterDto stageFilterDto) {
        return stageFilterDto != null && StringUtils.isNotBlank(stageFilterDto.getTeamRolePattern());
    }

    @Override
    public Stream<Stage> apply(Stream<Stage> stage, StageFilterDto stageFilterDto) {
        if (stageFilterDto == null || StringUtils.isBlank(stageFilterDto.getTeamRolePattern())) {
            return stage;
        }

        String teamRolePattern = stageFilterDto.getTeamRolePattern().toLowerCase();
        return stage.filter(stage1 ->
                stage1.getStageRoles().stream()
                        .anyMatch(stageRole ->
                                stageRole.getTeamRole().name().toLowerCase().contains(teamRolePattern)
                        )
        );
    }
}
