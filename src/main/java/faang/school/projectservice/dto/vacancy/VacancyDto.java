package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class VacancyDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private TeamRole position;

    @NotNull
    private Long project;

    @NotNull
    private VacancyStatus status;

    private Integer count;

    private List<Long> candidateIds;

    private Long createdBy;

    private Long updatedBy;

    private Double salary;

    private WorkSchedule workSchedule;

    private List<Long> requiredSkillIds;
}
