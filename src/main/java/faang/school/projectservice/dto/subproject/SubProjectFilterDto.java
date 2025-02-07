package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.model.ProjectStatus;
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
public class SubProjectFilterDto implements FilterDto {

    private String namePattern;
    private ProjectStatus status;
}

