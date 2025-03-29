package ru.practicum.helpers;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PaginateHelper {
    public PageRequest getPageRequest(Integer from, Integer size, Sort sort) {
        return PageRequest.of(from / size, size, sort);
    }
}
