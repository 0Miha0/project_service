package faang.school.projectservice.dto.stage;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ActionWithTaskDto {
    private String action;
    private Long transferStageId;
}
