package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(source = "project.id", target = "project")
    @Mapping(source = "candidates", target = "candidateIds", qualifiedByName = "candidateIds")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(source = "project", target = "project.id")
    @Mapping(target = "candidates", ignore = true)
    Vacancy toEntity(VacancyDto vacancyDto);

    @Named("candidateIds")
    default List<Long> candidateIds(List<Candidate> candidates) {
        if (candidates == null) {
            return null;
        }
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }
}
