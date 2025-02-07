package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentDto {

    @NotBlank
    private String name;

    private String description;
    private LocalDateTime date;

    @NotNull
    private List<Long> projectIds;

    @NotNull
    private List<Long> userIds;
}
