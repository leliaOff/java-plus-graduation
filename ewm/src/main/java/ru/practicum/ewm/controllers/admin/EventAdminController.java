package ru.practicum.ewm.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventAdminFilterDto;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.services.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
@Slf4j
public class EventAdminController {
    private final EventService eventService;


    @GetMapping
    public List<EventFullDto> getEventsForAdmin(@RequestParam(required = false) List<Long> users,
                                                @RequestParam(required = false) List<String> states,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        EventAdminFilterDto filterDto = new EventAdminFilterDto(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size
        );
        List<EventFullDto> dto = eventService.getAdminEvents(filterDto);

        log.info("getting admin events for {}", dto);
        return eventService.getAdminEvents(filterDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventForAdmin(@PathVariable("eventId") Long eventId,
                                            @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Updating event with id={} for admin", eventId);
        return eventService.updateAdminEvent(eventId, updateEventAdminRequest);
    }
}