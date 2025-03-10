package ru.practicum.ewm.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.eventComment.*;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.models.EventComment;
import ru.practicum.ewm.models.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventCommentMapper {
    public EventCommentDto toDto(EventComment model) {
        return new EventCommentDto(
                model.getId(),
                model.getText(),
                UserMapper.toShortDto(model.getAuthor()),
                model.getCreated()
        );
    }

    public EventCommentPrivateDto toPrivateDto(EventComment model) {
        return new EventCommentPrivateDto(
                model.getId(),
                model.getText(),
                model.getEvent().getId(),
                model.getAuthor().getId(),
                model.getCreated(),
                model.getStatus()
        );
    }

    public EventComment toModel(CreateCommentRequest request, User author, Event event) {
        EventComment model = new EventComment();
        model.setEvent(event);
        model.setAuthor(author);
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
