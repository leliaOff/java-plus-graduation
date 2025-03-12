package ru.practicum.ewm.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFilterDto;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.enums.EventSort;
import ru.practicum.ewm.services.EventService;
import ru.practicum.ewm.services.StatEventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventController {
    private final EventService eventService;
    private final StatEventService statEventService;

    @GetMapping
    public Collection<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                               @RequestParam(required = false) Boolean onlyAvailable,
                                               @RequestParam(defaultValue = "EVENT_DATE") EventSort sort,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               HttpServletRequest request) {
        Collection<EventShortDto> events = eventService.getEvents(new EventFilterDto(text, categories, paid, rangeStart, rangeEnd, onlyAvailable),
                sort,
                from,
                size
        );
        statEventService.hit(request.getRequestURI(), request.getRemoteAddr());
        return events;
    }

    @GetMapping("/{id}")
    public EventFullDto find(@PathVariable Long id, HttpServletRequest request) {
        EventFullDto event = eventService.find(id);
        statEventService.hit(request.getRequestURI(), request.getRemoteAddr());
        return event;
    }
}
