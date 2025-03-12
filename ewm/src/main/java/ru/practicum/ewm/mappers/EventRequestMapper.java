package ru.practicum.ewm.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.eventRequest.ParticipationRequestDto;
import ru.practicum.ewm.models.EventRequest;

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
