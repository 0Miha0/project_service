package faang.school.projectservice.dto.internship;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class InternshipCreationDto {

    @NotBlank(message = "The internship name cannot be blank!")
    private String name;

    @NotBlank(message = "The internship description cannot be empty!")
    private String description;

    @NotNull
    private Long projectId;

    @NotNull(message = "The internship team must have a mentor!")
    private Long mentorUserId;

    @NotNull(message = "The created internship must have a creator!")
    private Long creatorUserId;

    @NotEmpty(message = "The list of interns cannot be empty!")
    private List<@NotNull Long> internUserIds;

    @NotNull(message = "The internship start date cannot be null!")
    @Future(message = "The internship start date must be in the future!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "The internship end date cannot be null!")
    @Future(message = "The internship end date must be in the future!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
}
