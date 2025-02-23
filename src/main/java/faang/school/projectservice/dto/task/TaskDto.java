package faang.school.projectservice.dto.task;

import faang.school.projectservice.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private Long id;
    private String name;
    private String description;
    private TaskStatus status;
    private Long performerUserId;
    private Long reporterUserId;
    private Integer minutesTracked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long parentTaskId;
    private List<Long> linkedTasksIds;
    private Long projectId;
    private Long stageId;
}
