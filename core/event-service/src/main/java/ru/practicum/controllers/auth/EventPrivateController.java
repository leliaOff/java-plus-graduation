package ru.practicum.controllers.auth;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.services.EventService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventDto addEvent(@PathVariable("userId") Long userId, @Valid @RequestBody NewEventDto event) {
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
    EventDto getEvent(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId) {
        return eventService.getPrivateEvent(userId, eventId);
    }

    @PatchMapping("{eventId}")
    EventDto updateEvent(@PathVariable("userId") Long userId,
                         @PathVariable("eventId") Long eventId,
                         @Valid @RequestBody UpdateEventUserRequest event) {
        return eventService.updatePrivateEvent(userId, eventId, event);
    }
}
