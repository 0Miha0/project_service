package faang.school.projectservice.dto.stage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageDto {

    private Long stageId;

    private String stageName;

    private Long projectId;

    private List<Long> stageRolesId;

    private List<Long> executorsId;
}