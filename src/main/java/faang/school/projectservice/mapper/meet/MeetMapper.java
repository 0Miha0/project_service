package faang.school.projectservice.mapper.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.model.Meet;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MeetMapper {

    @Mapping(target = "project.id", source = "project")
    Meet toEntity(MeetDto dto);

    @Mapping(target = "project", source = "project.id")
    MeetDto toDto(Meet entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "project.id", source = "project")
    void updateMeetFromDto(@MappingTarget Meet meet, MeetDto dto);

    List<MeetDto> toDtoList(List<Meet> meets);
}
