package ru.practicum.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.eventComment.*;
import ru.practicum.enums.EventCommentStatus;
import ru.practicum.enums.EventState;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.exceptions.InvalidDataException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.helpers.PaginateHelper;
import ru.practicum.mappers.EventCommentMapper;
import ru.practicum.models.Event;
import ru.practicum.models.EventComment;
import ru.practicum.repositories.EventCommentRepository;
import ru.practicum.repositories.EventRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventCommentService {
    private final EventRepository eventRepository;
    private final EventCommentRepository eventCommentRepository;
    private final UserEventService userEventService;

    public List<EventCommentPrivateDto> getComments(Long userId, Long eventId, Integer size, Integer from) {
        if (eventId != null) {
            return eventCommentRepository.findAllByAuthorIdAndEventId(userId,
                            eventId,
                            PaginateHelper.getPageRequest(from, size, Sort.by(Sort.Direction.ASC, "created"))
                    ).stream()
                    .map(EventCommentMapper::toPrivateDto)
                    .collect(Collectors.toList());
        }
        return eventCommentRepository.findAllByAuthorId(userId,
                        PaginateHelper.getPageRequest(from, size, Sort.by(Sort.Direction.ASC, "created"))
                ).stream()
                .map(EventCommentMapper::toPrivateDto)
                .collect(Collectors.toList());
    }

    public List<EventCommentPrivateDto> getComments(Long eventId, Integer size, Integer from) {
        if (eventId != null) {
            return eventCommentRepository.findAllByEventId(eventId,
                            PaginateHelper.getPageRequest(from, size, Sort.by(Sort.Direction.ASC, "created"))
                    ).stream()
                    .map(EventCommentMapper::toPrivateDto)
                    .collect(Collectors.toList());
        }
        return eventCommentRepository.findAll(PaginateHelper.getPageRequest(from, size, Sort.by(Sort.Direction.ASC, "created"))).stream()
                .map(EventCommentMapper::toPrivateDto)
                .collect(Collectors.toList());
    }

    public List<EventCommentDto> getPublishedComments(Long eventId, Integer size, Integer from) {
        List<EventComment> eventCommentList = eventCommentRepository.findAllByEventIdAndStatus(eventId, EventCommentStatus.PUBLISHED,
                PaginateHelper.getPageRequest(from, size, Sort.by(Sort.Direction.ASC, "created"))
        );
        Map<Long, UserDto> users = userEventService.getUsersByComments(eventCommentList);
        return eventCommentRepository.findAllByEventIdAndStatus(eventId, EventCommentStatus.PUBLISHED,
                        PaginateHelper.getPageRequest(from, size, Sort.by(Sort.Direction.ASC, "created"))
                ).stream()
                .map((EventComment model) -> EventCommentMapper.toDto(model, users.get(model.getAuthorId())))
                .collect(Collectors.toList());
    }

    public EventComment find(Long eventId) {
        return eventCommentRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event's comment not found"));
    }


    @Transactional
    public EventCommentPrivateDto create(Long userId, CreateCommentRequest request) {
        UserDto author = userEventService.getUser(userId);
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new InvalidDataException("Event not published");
        }
        EventComment comment = eventCommentRepository.save(EventCommentMapper.toModel(request, author.id(), event));
        return EventCommentMapper.toPrivateDto(comment);
    }


    @Transactional
    public EventCommentPrivateDto update(Long userId, Long commentId, UpdateCommentRequest request) {
        EventComment comment = find(commentId);
        if (!comment.getAuthorId().equals(userId)) {
            throw new ForbiddenException("No access to comment");
        }
        if (!comment.getStatus().equals(EventCommentStatus.PENDING)) {
            throw new BadRequestException("The comment must be in draft");
        }
        comment = eventCommentRepository.save(EventCommentMapper.mergeModel(request, comment));
        return EventCommentMapper.toPrivateDto(comment);
    }


    @Transactional
    public EventCommentPrivateDto update(Long commentId, UpdateCommentAdminRequest request) {
        EventComment comment = find(commentId);
        comment = eventCommentRepository.save(EventCommentMapper.mergeModel(request, comment));
        return EventCommentMapper.toPrivateDto(comment);
    }

    @Transactional
    public void delete(Long userId, Long commentId) {
        EventComment comment = find(commentId);
        if (!comment.getAuthorId().equals(userId)) {
            throw new ForbiddenException("No access to comment");
        }
        if (!comment.getStatus().equals(EventCommentStatus.PENDING)) {
            throw new BadRequestException("The comment must be in draft");
        }
        eventCommentRepository.delete(comment);
    }
}
