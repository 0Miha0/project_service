package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipStatusFilter implements Filter<Internship, InternshipFilterDto> {

    @Override
    public boolean isApplicable(InternshipFilterDto filterDto) {
        return filterDto.getInternshipStatus() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, InternshipFilterDto filterDto) {
        return internships.filter(internship -> internship.getStatus().equals(filterDto.getInternshipStatus()));
    }
}