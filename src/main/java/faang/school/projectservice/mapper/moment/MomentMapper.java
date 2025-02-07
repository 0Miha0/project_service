package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {
    Moment toEntity(MomentDto momentDto);

    @Mapping(target = "projectIds", expression = "java(projectToIds(moment.getProjects()))")
    MomentDto toDto(Moment moment);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Moment moment, MomentDto momentDto);

    default List<Long> projectToIds(List<Project> projects) {
        return projects.stream()
                .map(Project::getId)
                .toList();
    }

}
