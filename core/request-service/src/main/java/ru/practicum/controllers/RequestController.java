package ru.practicum.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.CollectorClient;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.services.RequestService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;
    private final CollectorClient collectorClient;

    @GetMapping
    List<ParticipationRequestDto> getRequests(@PathVariable Long userId) {
        return requestService.getRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ParticipationRequestDto addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        ParticipationRequestDto dto = requestService.addRequest(userId, eventId);
        collectorClient.sendUserAction(userId, eventId, ActionTypeProto.ACTION_REGISTER);
        return dto;
    }

    @PatchMapping("/{requestId}/cancel")
    ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
