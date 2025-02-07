package faang.school.projectservice.dto.project;

import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.Size;
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
public class ProjectFilterDto implements FilterDto {

    @Size(max = 128)
    private String name;

    private ProjectStatus projectStatus;
}
