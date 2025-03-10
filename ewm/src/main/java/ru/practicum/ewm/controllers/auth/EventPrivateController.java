package ru.practicum.ewm.controllers.auth;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventUserRequest;
import ru.practicum.ewm.dto.eventRequest.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.eventRequest.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.eventRequest.ParticipationRequestDto;
import ru.practicum.ewm.services.EventService;
import ru.practicum.ewm.services.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;
    private final RequestService requestService;

    @Autowired
    public EventPrivateController(EventService eventService, RequestService requestService) {
        this.eventService = eventService;
        this.requestService = requestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto addEvent(@PathVariable("userId") Long userId, @Valid @RequestBody NewEventDto event) {
        log.info("Add event {}", event);
        return eventService.addEvent(userId, event);
    }

    @GetMapping
    List<EventShortDto> getEvents(@PathVariable("userId") Long userId,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestParam(defaultValue = "0") Integer from) {
        return eventService.getPrivateEvents(userId, PageRequest.of(from, size));
    }

    @GetMapping("{eventId}")
    EventFullDto getEvent(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {
        return eventService.getPrivateEvent(userId, eventId);
    }

    @PatchMapping("{eventId}")
    EventFullDto updateEvent(@PathVariable("userId") Long userId,
                             @PathVariable("eventId") Long eventId,
                             @Valid @RequestBody UpdateEventUserRequest event) {
        return eventService.updatePrivateEvent(userId, eventId, event);
    }

    @GetMapping("/{eventId}/requests")
    List<ParticipationRequestDto> getEventRequests(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {
        return requestService.getPrivateRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    EventRequestStatusUpdateResult updateEventRequestsStatus(@PathVariable("userId") Long userId,
                                                             @PathVariable("eventId") Long eventId,
                                                             @RequestBody(required = false) EventRequestStatusUpdateRequest request) {
        return requestService.updateEventRequestStatus(userId, eventId, request);
    }
}
