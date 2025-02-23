package faang.school.projectservice.dto.task;

import faang.school.projectservice.dto.filter.FilterDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskFilterDto implements FilterDto {
    private String descriptionPattern;
    private String statusPattern;
    private Long performerUserId;
}
