package faang.school.projectservice.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Vacancy;

import java.util.stream.Stream;

public class VacancyNameFilter implements Filter<Vacancy, VacancyFilterDto> {

    @Override
    public boolean isApplicable(VacancyFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto filters) {
        return vacancies.filter(vacancy ->
                vacancy.getName().toLowerCase().contains(filters.getNamePattern().toLowerCase()));
    }
}
