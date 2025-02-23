package faang.school.projectservice.mapper.task;

import faang.school.projectservice.dto.task.CreateUpdateTaskDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(source = "parentTask.id", target = "parentTaskId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "stage.stageId", target = "stageId")
    @Mapping(source = "linkedTasks", target = "linkedTasksIds")
    TaskDto toTaskDto(Task task);

    Task toEntity(CreateUpdateTaskDto dto);

    default List<Long> mapTasksToIds(List<Task> tasks) {
        if (tasks == null) {
            return null;
        }
        return tasks.stream()
                .map(Task::getId)
                .toList();
    }
}