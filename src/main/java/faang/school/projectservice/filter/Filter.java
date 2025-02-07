package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.FilterDto;

import java.util.stream.Stream;

public interface Filter<T, F extends FilterDto> {
    boolean isApplicable(F filterDto);

    Stream<T> apply(Stream<T> objects, F filterDto);
}
