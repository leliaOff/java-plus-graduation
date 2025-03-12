package ru.practicum.ewm.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.eventComment.*;
import ru.practicum.ewm.enums.EventCommentStatus;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.exceptions.BadRequestException;
import ru.practicum.ewm.exceptions.ForbiddenException;
import ru.practicum.ewm.exceptions.InvalidDataException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.helpers.PaginateHelper;
import ru.practicum.ewm.mappers.EventCommentMapper;
import ru.practicum.ewm.models.Event;
import ru.practicum.ewm.models.EventComment;
import ru.practicum.ewm.models.User;
import ru.practicum.ewm.repositories.EventCommentRepository;
import ru.practicum.ewm.repositories.EventRepository;
import ru.practicum.ewm.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class EventCommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventCommentRepository eventCommentRepository;

    public EventCommentService(UserRepository userRepository,
                               EventRepository eventRepository,
                               EventCommentRepository eventCommentRepository
    ) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventCommentRepository = eventCommentRepository;
    }

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
        return eventCommentRepository.findAllByEventIdAndStatus(eventId, EventCommentStatus.PUBLISHED,
                        PaginateHelper.getPageRequest(from, size, Sort.by(Sort.Direction.ASC, "created"))
                ).stream()
                .map(EventCommentMapper::toDto)
                .collect(Collectors.toList());
    }

    public EventComment find(Long eventId) {
        return eventCommentRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event's comment not found"));
    }


    @Transactional
    public EventCommentPrivateDto create(Long userId, CreateCommentRequest request) {
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(request.getEventId()).orElseThrow(() -> new NotFoundException("Event not found"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new InvalidDataException("Event not published");
        }
        EventComment comment = eventCommentRepository.save(EventCommentMapper.toModel(request, author, event));
        return EventCommentMapper.toPrivateDto(comment);
    }


    @Transactional
    public EventCommentPrivateDto update(Long userId, Long commentId, UpdateCommentRequest request) {
        EventComment comment = find(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
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
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("No access to comment");
        }
        if (!comment.getStatus().equals(EventCommentStatus.PENDING)) {
            throw new BadRequestException("The comment must be in draft");
        }
        eventCommentRepository.delete(comment);
    }
}
