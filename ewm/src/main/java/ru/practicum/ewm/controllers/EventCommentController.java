package ru.practicum.ewm.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.eventComment.EventCommentDto;
import ru.practicum.ewm.services.EventCommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events/{eventId}/comments")
public class EventCommentController {

    private final EventCommentService eventCommentService;

    @Autowired
    public EventCommentController(EventCommentService eventCommentService) {
        this.eventCommentService = eventCommentService;
    }

    @GetMapping
    List<EventCommentDto> get(@PathVariable("eventId") Long eventId,
                              @RequestParam(defaultValue = "10") Integer size,
                              @RequestParam(defaultValue = "0") Integer from) {
        return eventCommentService.getPublishedComments(eventId, size, from);
    }
}
