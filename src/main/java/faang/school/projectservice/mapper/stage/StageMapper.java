package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.service.ProjectService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ProjectService.class})
public interface StageMapper {

    @Mapping(source = "stageRoles", target = "stageRolesId", qualifiedByName = "mapStageToIds")
    @Mapping(source = "executors", target = "executorsId", qualifiedByName = "mapTeamMemberToIds")
    @Mapping(source = "project", target = "projectId", qualifiedByName = "mapProjectToIds")
    StageDto toDto(Stage stage);

    Stage toEntity(StageDto stageDto);

    @Mapping(target = "teamRolePattern", source = "stageRoles", qualifiedByName = "stageRolesToPattern")
    @Mapping(target = "taskStatusPattern", source = "tasks", qualifiedByName = "taskStatusToPattern")
    StageFilterDto toFilterDto(Stage stage);

    Stage toEntityFromFilter(StageFilterDto stageFilterDto);

    @Named("mapStageToIds")
    default List<Long> mapStageToIds(List<StageRoles> stages) {
        return stages.stream().map(StageRoles::getId).toList();
    }

    @Named("mapTeamMemberToIds")
    default List<Long> mapTeamMemberToIds(List<TeamMember> teamMembers) {
        return teamMembers.stream().map(TeamMember::getId).toList();
    }

    @Named("mapProjectToIds")
    default Long mapProjectToId(Project project) {
        return project != null ? project.getId() : null;
    }

    @Named("stageRolesToPattern")
    default String stageRolesToPattern(List<StageRoles> stageRoles) {
        if (stageRoles == null || stageRoles.isEmpty()) {
            return null;
        }

        return stageRoles.stream()
                .map(role -> role.getTeamRole().name())
                .collect(Collectors.joining(","));
    }

    @Named("taskStatusToPattern")
    default String taskStatusToPattern(List<Task> taskStatus) {
        if (taskStatus == null || taskStatus.isEmpty()) {
            return null;
        }

        return taskStatus.stream()
                .map(task -> task.getStatus().name())
                .collect(Collectors.joining(","));
    }
}
