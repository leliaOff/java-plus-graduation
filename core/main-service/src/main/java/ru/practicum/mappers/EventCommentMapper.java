package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.eventComment.*;
import ru.practicum.models.Event;
import ru.practicum.models.EventComment;

import java.time.LocalDateTime;

@UtilityClass
public class EventCommentMapper {
    public EventCommentDto toDto(EventComment model, UserDto user) {
        return new EventCommentDto(
                model.getId(),
                model.getText(),
                UserDtoMapper.toShortDto(user),
                model.getCreated()
        );
    }

    public EventCommentPrivateDto toPrivateDto(EventComment model) {
        return new EventCommentPrivateDto(
                model.getId(),
                model.getText(),
                model.getEvent().getId(),
                model.getId(),
                model.getCreated(),
                model.getStatus()
        );
    }

    public EventComment toModel(CreateCommentRequest request, Long authorId, Event event) {
        EventComment model = new EventComment();
        model.setEvent(event);
        model.setAuthorId(authorId);
        model.setText(request.getText());
        model.setCreated(LocalDateTime.now());
        return model;
    }

    public EventComment mergeModel(UpdateCommentRequest request, EventComment model) {
        model.setText(request.getText());
        return model;
    }

    public EventComment mergeModel(UpdateCommentAdminRequest request, EventComment model) {
        if (request.getText() != null) {
            model.setText(request.getText());
        }
        if (request.getStatus() != null) {
            model.setStatus(request.getStatus());
        }
        return model;
    }
}
