package ru.practicum.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.clients.UserClient;
import ru.practicum.dto.UserDto;
import ru.practicum.models.Event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserEventService {
    private final UserClient client;

    public UserDto getUser(Long id) {
        return client.getUser(id);
    }

    public Map<Long, UserDto> getUsersByEvents(Collection<Event> events) {
        if (events == null || events.isEmpty()) {
            return new HashMap<>();
        }
        Set<Long> eventsInitiatorList = events
                .stream()
                .map(Event::getInitiatorId)
                .collect(Collectors.toSet());
        return client.getUsers(eventsInitiatorList.stream().toList()).stream()
                .collect(Collectors.toMap(UserDto::id, Function.identity()));
    }
}
