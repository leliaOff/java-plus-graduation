package ru.practicum.ewm.controllers.admin;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.eventComment.EventCommentPrivateDto;
import ru.practicum.ewm.dto.eventComment.UpdateCommentAdminRequest;
import ru.practicum.ewm.services.EventCommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/comments")
public class EventCommentAdminController {

    private final EventCommentService eventCommentService;

    @Autowired
    public EventCommentAdminController(EventCommentService eventCommentService) {
        this.eventCommentService = eventCommentService;
    }

    @GetMapping
    List<EventCommentPrivateDto> get(@RequestParam(required = false) Long eventId,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(defaultValue = "0") Integer from) {
        return eventCommentService.getComments(eventId, size, from);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    EventCommentPrivateDto update(@PathVariable("commentId") Long commentId,
                                  @Valid @RequestBody UpdateCommentAdminRequest request
    ) {
        return eventCommentService.update(commentId, request);
    }
}
