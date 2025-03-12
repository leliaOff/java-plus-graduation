package ru.practicum.ewm.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.location.Location;
import ru.practicum.ewm.enums.EventAdminStateAction;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.EventUserStateAction;
import ru.practicum.ewm.exceptions.InvalidDataException;
import ru.practicum.ewm.models.Category;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.models.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public EventShortDto toShortDto(Event model, Long view) {
        return new EventShortDto(
                model.getId(),
                model.getTitle(),
                model.getAnnotation(),
                CategoryMapper.toDto(model.getCategory()),
                model.getConfirmedRequests(),
                model.getEventDate(),
                UserMapper.toShortDto(model.getInitiator()),
                model.getPaid(),
                view
        );
    }


    public EventFullDto toDto(Event model, Long view) {
        return new EventFullDto(
                model.getId(),
                model.getTitle(),
                model.getAnnotation(),
                CategoryMapper.toDto(model.getCategory()),
                model.getConfirmedRequests(),
                model.getCreatedOn(),
                model.getDescription(),
                model.getEventDate(),
                UserMapper.toShortDto(model.getInitiator()),
                new Location(model.getLat(), model.getLon()),
                model.getPaid(),
                model.getParticipantLimit(),
                model.getPublishedOn(),
                model.getRequestModeration(),
                model.getState(),
                view
        );
    }


    public Event toModel(NewEventDto dto, User user) {
        Event model = new Event();
        model.setTitle(dto.getTitle());
        model.setAnnotation(dto.getAnnotation());
        model.setCategory(new Category(dto.getCategory()));
        model.setDescription(dto.getDescription());
        model.setEventDate(dto.getEventDate());
        model.setPaid(dto.getPaid());
        model.setParticipantLimit(dto.getParticipantLimit());
        model.setRequestModeration(dto.getRequestModeration());
        model.setLat(dto.getLocation().getLat());
        model.setLon(dto.getLocation().getLon());
        model.setInitiator(user);
        model.setCreatedOn(LocalDateTime.now());
        return model;
    }

    public EventFullDto toDtoWithoutViews(Event model) {
        return new EventFullDto(
                model.getId(),
                model.getTitle(),
                model.getAnnotation(),
                CategoryMapper.toDto(model.getCategory()),
                model.getConfirmedRequests(),
                model.getCreatedOn(),
                model.getDescription(),
                model.getEventDate(),
                UserMapper.toShortDto(model.getInitiator()),
                new Location(model.getLat(), model.getLon()),
                model.getPaid(),
                model.getParticipantLimit(),
                model.getPublishedOn(),
                model.getRequestModeration(),
                model.getState(),
                0L
        );
    }

    public Event mergeModel(Event model, UpdateEventAdminRequest dto) {
        if (dto.getTitle() != null) {
            model.setTitle(dto.getTitle());
        }
        if (dto.getAnnotation() != null) {
            model.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            model.setCategory(new Category(dto.getCategory()));
        }
        if (dto.getDescription() != null) {
            model.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            model.setEventDate(dto.getEventDate());
        }
        if (dto.getPaid() != null) {
            model.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            model.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            model.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getLocation() != null) {
            model.setLat(dto.getLocation().getLat());
            model.setLon(dto.getLocation().getLon());
        }
        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(EventAdminStateAction.PUBLISH_EVENT)) {
                model.setState(EventState.PUBLISHED);
                model.setPublishedOn(LocalDateTime.now());
            } else if (dto.getStateAction().equals(EventAdminStateAction.REJECT_EVENT)) {
                model.setState(EventState.CANCELED);
            } else {
                throw new InvalidDataException(dto.getStateAction() + " is not supported");
            }
        }
        return model;
    }

    public static Event mergeModel(Event model, UpdateEventUserRequest dto) {
        if (dto.getTitle() != null) {
            model.setTitle(dto.getTitle());
        }
        if (dto.getAnnotation() != null) {
            model.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null) {
            model.setCategory(new Category(dto.getCategory()));
        }
        if (dto.getDescription() != null) {
            model.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            model.setEventDate(dto.getEventDate());
        }
        if (dto.getPaid() != null) {
            model.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            model.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            model.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getLocation() != null) {
            model.setLat(dto.getLocation().getLat());
            model.setLon(dto.getLocation().getLon());
        }
        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(EventUserStateAction.SEND_TO_REVIEW)) {
                model.setState(EventState.PENDING);
            } else if (dto.getStateAction().equals(EventUserStateAction.CANCEL_REVIEW)) {
                model.setState(EventState.CANCELED);
            } else {
                throw new InvalidDataException(dto.getStateAction() + " is not supported");
            }
        }
        return model;
    }
}
