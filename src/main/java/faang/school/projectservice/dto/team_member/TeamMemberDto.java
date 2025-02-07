package faang.school.projectservice.dto.team_member;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class TeamMemberDto {

    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotNull(message = "Role list must not be null")
    @Size(min = 1, message = "At least one role must be specified")
    private List<String> role;

    @NotNull(message = "Team ID must not be null")
    private Long team;
}
