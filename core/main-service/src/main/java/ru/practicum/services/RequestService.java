package ru.practicum.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.eventRequest.EventRequestStatusUpdateRequest;
import ru.practicum.dto.eventRequest.EventRequestStatusUpdateResult;
import ru.practicum.dto.eventRequest.ParticipationRequestDto;
import ru.practicum.enums.EventRequestStatus;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.InvalidDataException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.EventRequestMapper;
import ru.practicum.models.Event;
import ru.practicum.models.EventRequest;
import ru.practicum.models.User;
import ru.practicum.repositories.EventRepository;
import ru.practicum.repositories.RequestRepository;
import ru.practicum.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public RequestService(RequestRepository requestRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User user = getUser(userId);
        Event event = getEvent(eventId);
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId))
            throw new InvalidDataException("Request already exist");
        if (event.getInitiator().getId().equals(userId)) {
            throw new InvalidDataException("Request can't be created by initiator");
        }
        if (event.getPublishedOn() == null) {
            throw new InvalidDataException("Event not yet published");
        }

        int requestsSize = requestRepository.findAllByEventId(eventId).size();
        if (event.getParticipantLimit() > 0 && !event.getRequestModeration() && event.getParticipantLimit() <= requestsSize) {
            throw new InvalidDataException("Participant limit exceeded");
        }

        EventRequest eventRequest = new EventRequest(null, event, user, LocalDateTime.now(), EventRequestStatus.PENDING);
        if (!event.getRequestModeration()) {
            eventRequest.setStatus(EventRequestStatus.CONFIRMED);
        }

        if (event.getParticipantLimit() != null && event.getParticipantLimit() == 0) {
            eventRequest.setStatus(EventRequestStatus.CONFIRMED);
        }

        return EventRequestMapper.toDto(requestRepository.save(eventRequest));
    }

    public List<ParticipationRequestDto> getRequests(Long userId) {
        getUser(userId);
        return EventRequestMapper.toDto(requestRepository.findAllByRequesterId(userId));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        EventRequest request = requestRepository.findByRequesterIdAndId(userId, requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        request.setStatus(EventRequestStatus.CANCELED);
        return EventRequestMapper.toDto(requestRepository.save(request));
    }

    public List<ParticipationRequestDto> getPrivateRequests(Long userId, Long eventId) {
        return EventRequestMapper.toDto(requestRepository.findAllByEventIdWithInitiatorId(eventId, userId));
    }

    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest updateRequest) {
        Event event = getEvent(eventId);
        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        if (event.getParticipantLimit() == 0) {
            return updateResult;
        }

        List<EventRequest> eventRequests = requestRepository.findAllByEventIdWithInitiatorId(eventId, userId);
        if (eventRequests.isEmpty()) {
            return updateResult;
        }

        List<EventRequest> requestUpdateList = eventRequests.stream()
                .filter(eventRequest -> updateRequest.getRequestIds().contains(eventRequest.getId())).toList();
        requestUpdateList.forEach(eventRequest -> {
            if (eventRequest.getStatus().equals(EventRequestStatus.CONFIRMED)) {
                throw new ConflictException("Request can't REJECT confirmed request");
            }
            if (!eventRequest.getStatus().equals(EventRequestStatus.PENDING)) {
                throw new BadRequestException("Request must have status PENDING");
            }

            if (event.getConfirmedRequests() != null && (event.getConfirmedRequests() >= event.getParticipantLimit())) {
                eventRequest.setStatus(EventRequestStatus.REJECTED);
                updateResult.getRejectedRequests().add(EventRequestMapper.toDto(eventRequest));
                requestRepository.save(eventRequest);
                throw new ConflictException("The participant limit has been reached");
            }
            eventRequest.setStatus(updateRequest.getStatus());
            if (updateRequest.getStatus().equals(EventRequestStatus.REJECTED)) {
                updateResult.getRejectedRequests().add(EventRequestMapper.toDto(eventRequest));
            } else {
                updateResult.getConfirmedRequests().add(EventRequestMapper.toDto(eventRequest));
            }
            requestRepository.save(eventRequest);
            event.setConfirmedRequests(event.getConfirmedRequests() == null ? 1 : event.getConfirmedRequests() + 1);
        });
        eventRepository.save(event);
        return updateResult;
    }
}
