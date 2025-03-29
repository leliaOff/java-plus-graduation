package ru.practicum.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventRequestStatusUpdateRequest;
import ru.practicum.dto.EventRequestStatusUpdateResult;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.services.RequestService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/{userId}/events/{eventId}/requests")
public class EventRequestController {

    private final RequestService requestService;

    @GetMapping
    List<ParticipationRequestDto> getEventRequests(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping
    EventRequestStatusUpdateResult updateEventRequestsStatus(@PathVariable("userId") Long userId,
                                                             @PathVariable("eventId") Long eventId,
                                                             @RequestBody(required = false) EventRequestStatusUpdateRequest request) {
        return requestService.updateEventRequestStatus(userId, eventId, request);
    }
}
