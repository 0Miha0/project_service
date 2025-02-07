package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.filter.FilterDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageFilterDto implements FilterDto {

    private String teamRolePattern;

    private String taskStatusPattern;
}
