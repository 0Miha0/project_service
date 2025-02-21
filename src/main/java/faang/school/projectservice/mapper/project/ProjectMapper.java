package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProjectMapper {

    Project toCreateEntity(ProjectCreateDto dto);

    ProjectCreateDto toCreateDto(Project entity);

    Project toEntity(ProjectDto dto);

    ProjectUpdateDto toDto(Project entity);
}
