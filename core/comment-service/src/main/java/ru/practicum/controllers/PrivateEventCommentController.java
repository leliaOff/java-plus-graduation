package ru.practicum.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CreateCommentRequest;
import ru.practicum.dto.EventCommentPrivateDto;
import ru.practicum.dto.UpdateCommentRequest;
import ru.practicum.services.EventCommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/comments")
public class PrivateEventCommentController {

    private final EventCommentService eventCommentService;

    @Autowired
    public PrivateEventCommentController(EventCommentService eventCommentService) {
        this.eventCommentService = eventCommentService;
    }

    @GetMapping
    List<EventCommentPrivateDto> get(@PathVariable("userId") Long userId,
                                     @RequestParam(required = false) Long eventId,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(defaultValue = "0") Integer from) {
        return eventCommentService.getComments(userId, eventId, size, from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    EventCommentPrivateDto create(@PathVariable("userId") Long userId, @Valid @RequestBody CreateCommentRequest request) {
        return eventCommentService.create(userId, request);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    EventCommentPrivateDto update(@PathVariable("userId") Long userId,
                                  @PathVariable("commentId") Long commentId,
                                  @Valid @RequestBody UpdateCommentRequest request
    ) {
        return eventCommentService.update(userId, commentId, request);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("userId") Long userId, @PathVariable("commentId") Long commentId) {
        eventCommentService.delete(userId, commentId);
    }
}
