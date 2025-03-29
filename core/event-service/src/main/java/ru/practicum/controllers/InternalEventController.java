package ru.practicum.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.clients.requests.SetEventConfirmedRequest;
import ru.practicum.dto.EventDto;
import ru.practicum.services.EventService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/internal/event/{id}")
public class InternalEventController {
    private final EventService eventService;

    @GetMapping
    EventDto getEvent(@PathVariable Long id) {
        return eventService.find(id);
    }

    @PostMapping
    void setConfirmed(@PathVariable Long id, @Valid @RequestBody SetEventConfirmedRequest request) {
        eventService.setConfirmed(id, request.getConfirmedRequests());
    }
}
