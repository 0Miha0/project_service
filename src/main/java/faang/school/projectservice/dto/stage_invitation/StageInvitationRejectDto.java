package faang.school.projectservice.dto.stage_invitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class StageInvitationRejectDto {

    @NotNull(message = "Stage invitation Id can't be null")
    private Long stageInvitationId;

    @NotNull(message = "Invited Id can't be null")
    private Long invitedId;

    @NotNull(message = "Reject reason can't be null")
    @NotBlank(message = "Reject reason can't be empty")
    private String rejectReason;
}