package faang.school.projectservice.dto.stage_invitation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class StageInvitationDto {
    private Long id;
    private String description;

    @NotNull(message = "Stage id can't be null")
    private Long stageId;

    @NotNull(message = "Author Id can't be null")
    private Long authorId;

    @NotNull(message = "Invited Id can't be null")
    private Long invitedId;
}
