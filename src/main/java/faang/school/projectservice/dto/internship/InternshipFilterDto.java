package faang.school.projectservice.dto.internship;

import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class InternshipFilterDto implements FilterDto {

    private InternshipStatus internshipStatus;
    private TeamRole teamRole;
}

