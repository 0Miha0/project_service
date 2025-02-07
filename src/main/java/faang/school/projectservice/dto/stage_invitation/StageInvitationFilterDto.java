package faang.school.projectservice.dto.stage_invitation;

import faang.school.projectservice.dto.filter.FilterDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationFilterDto implements FilterDto {
    private String descriptionPattern;
    private String stageNamePattern;
    private String projectNamePattern;
}
