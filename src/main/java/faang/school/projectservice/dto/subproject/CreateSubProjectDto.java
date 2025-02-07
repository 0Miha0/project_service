package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateSubProjectDto {

    @NotBlank
    @Size(max = 128)
    private String name;

    @NotBlank
    @Size(max = 4096)
    private String description;

    @NotNull
    private ProjectVisibility visibility;
}
