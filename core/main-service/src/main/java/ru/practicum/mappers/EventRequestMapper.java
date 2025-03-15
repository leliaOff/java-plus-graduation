package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.eventRequest.ParticipationRequestDto;
import ru.practicum.models.EventRequest;

import java.util.List;

@UtilityClass
public class EventRequestMapper {
    public ParticipationRequestDto toDto(EventRequest model) {
        return new ParticipationRequestDto(
                model.getId(),
                model.getCreated(),
                model.getEvent().getId(),
                model.getRequester().getId(),
                model.getStatus()
        );
    }

    public List<ParticipationRequestDto> toDto(List<EventRequest> models) {
        return models.stream().map(EventRequestMapper::toDto).toList();
    }
}
