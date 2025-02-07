package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Project;

import java.util.Objects;
import java.util.stream.Stream;

public class SubProjectNameFilter implements Filter<Project, SubProjectFilterDto> {

    @Override
    public boolean isApplicable(SubProjectFilterDto filters) {
        return filters != null && filters.getNamePattern() != null && !filters.getNamePattern().isBlank();
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, SubProjectFilterDto filters) {
        Objects.requireNonNull(projects, "Projects stream cannot be null");
        Objects.requireNonNull(filters, "Filters cannot be null");

        return projects.filter(project ->
                project.getName().toLowerCase().contains(filters.getNamePattern().toLowerCase())
        );
    }
}