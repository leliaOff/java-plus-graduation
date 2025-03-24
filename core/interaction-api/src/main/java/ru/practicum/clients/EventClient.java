package ru.practicum.clients;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.clients.requests.SetEventConfirmedRequest;
import ru.practicum.dto.EventDto;

@FeignClient(name = "event-service")
public interface EventClient {
    @GetMapping("/internal/event/{id}")
    EventDto getEvent(@PathVariable Long id);

    @PostMapping("/internal/event/{id}")
    void setConfirmed(@PathVariable Long id, @Valid @RequestBody SetEventConfirmedRequest request);
}