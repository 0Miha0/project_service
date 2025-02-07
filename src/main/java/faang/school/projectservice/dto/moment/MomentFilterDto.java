package faang.school.projectservice.dto.moment;

import faang.school.projectservice.dto.filter.FilterDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentFilterDto implements FilterDto {
    private LocalDateTime datePattern;
    private String projectsPattern;
}
