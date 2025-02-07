package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class MomentProjectsFilter implements Filter<Moment, MomentFilterDto> {

    @Override
    public boolean isApplicable(MomentFilterDto filters) {
        return filters != null && !filters.getProjectsPattern().isBlank();
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters) {
        return moments.filter(moment ->
                moment.getProjects().stream()
                        .map(Project::getName)
                        .anyMatch(name -> name.equals(filters.getProjectsPattern()))
        );
    }

}
