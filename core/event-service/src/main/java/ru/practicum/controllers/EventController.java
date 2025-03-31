package ru.practicum.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.CollectorClient;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.event.EventFilterDto;
import ru.practicum.dto.event.EventRecommendationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.EventSort;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.services.EventAnalyzerService;
import ru.practicum.services.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventController {
    private final EventService eventService;
    private final EventAnalyzerService eventAnalyzerService;
    private final CollectorClient collectorClient;

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
        return eventService.getEvents(new EventFilterDto(text, categories, paid, rangeStart, rangeEnd, onlyAvailable),
                sort,
                from,
                size
        );
    }

    @GetMapping("/{id}")
    public EventDto find(@PathVariable Long id, @RequestHeader("X-EWM-USER-ID") Long userId) {
        EventDto event = eventService.findPublished(id);
        collectorClient.sendUserAction(userId, event.getId(), ActionTypeProto.ACTION_VIEW);
        return event;
    }

    @GetMapping("/recommendations")
    public List<EventRecommendationDto> recommendations(@RequestHeader("X-EWM-USER-ID") Long userId,
                                                        @RequestParam(defaultValue = "10") Integer limit
    ) {
        return eventAnalyzerService.recommendations(userId, limit);
    }

    @PutMapping("/{id}/like")
    public void like(@PathVariable Long id,
                     @RequestHeader("X-EWM-USER-ID") Long userId
    ) {
        collectorClient.sendUserAction(userId, id, ActionTypeProto.ACTION_LIKE);
    }
}
