package ru.practicum.services;

import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.clients.EventClient;
import ru.practicum.clients.UserClient;
import ru.practicum.clients.requests.SetEventConfirmedRequest;
import ru.practicum.dto.*;
import ru.practicum.enums.EventRequestStatus;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.InvalidDataException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.EventRequestMapper;
import ru.practicum.models.EventRequest;
import ru.practicum.repositories.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@EnableFeignClients
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventClient eventClient;
    private final UserClient userClient;

    public List<ParticipationRequestDto> getRequests(Long userId) {
        userClient.getUser(userId);
        return EventRequestMapper.toDto(requestRepository.findAllByRequesterId(userId));
    }

    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        UserDto user = userClient.getUser(userId);
        try {
            EventDto event = eventClient.getEvent(eventId);
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
            EventRequest eventRequest = new EventRequest(null, event.getId(), user.id(), LocalDateTime.now(), EventRequestStatus.PENDING);
            if (!event.getRequestModeration()) {
                eventRequest.setStatus(EventRequestStatus.CONFIRMED);
            }
            if (event.getParticipantLimit() != null && event.getParticipantLimit() == 0) {
                eventRequest.setStatus(EventRequestStatus.CONFIRMED);
            }
            return EventRequestMapper.toDto(requestRepository.save(eventRequest));
        } catch (FeignException exception) {
            throw new ConflictException("Event not found or not published");
        }
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userClient.getUser(userId);
        EventRequest request = requestRepository.findByRequesterIdAndId(userId, requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        request.setStatus(EventRequestStatus.CANCELED);
        return EventRequestMapper.toDto(requestRepository.save(request));
    }

    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        EventDto event = eventClient.getEvent(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            return new ArrayList<>();
        }
        return EventRequestMapper.toDto(requestRepository.findAllByEventId(eventId));
    }

    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestStatus(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest updateRequest) {
        EventDto event = eventClient.getEvent(eventId);
        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();
        if (event.getParticipantLimit() == 0 || !event.getInitiator().getId().equals(userId)) {
            return updateResult;
        }
        List<EventRequest> eventRequests = requestRepository.findAllByEventId(eventId);
        if (eventRequests.isEmpty()) {
            return updateResult;
        }

        SetEventConfirmedRequest request = new SetEventConfirmedRequest(
                event.getConfirmedRequests() == null ? 0 : event.getConfirmedRequests()
        );

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
            request.setConfirmedRequests(request.getConfirmedRequests() + 1);
        });
        eventClient.setConfirmed(event.getId(), request);
        return updateResult;
    }
}
