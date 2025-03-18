package ru.practicum.model;

import lombok.experimental.UtilityClass;
import ru.practicum.StatDto;

@UtilityClass
public class StatMapper {
    public StatDto toDto(Stat model) {
        StatDto dto = new StatDto();
        dto.setApp(model.getApp());
        dto.setUri(model.getUri());
        dto.setHits(model.getHits());
        return dto;
    }
}
