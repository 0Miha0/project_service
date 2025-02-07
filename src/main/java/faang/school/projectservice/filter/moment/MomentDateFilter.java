package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Stream;

@Component
public class MomentDateFilter implements Filter<Moment, MomentFilterDto> {

    @Override
    public boolean isApplicable(MomentFilterDto filters) {
        return filters.getDatePattern() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters) {
        return moments.filter(moment -> {
            LocalDate momentDate = moment.getDate().toLocalDate();
            LocalDate filterDate = filters.getDatePattern().toLocalDate();
            return momentDate.getYear() == filterDate.getYear() &&
                    momentDate.getMonthValue() == filterDate.getMonthValue()
                    && momentDate.getDayOfMonth() == filterDate.getDayOfMonth();
        });
    }

}
