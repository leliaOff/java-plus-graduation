package ru.practicum.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.clients.UserClient;
import ru.practicum.dto.UserDto;
import ru.practicum.models.EventComment;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserEventCommentService {
    private final UserClient client;

    public UserDto getUser(Long id) {
        return client.getUser(id);
    }

    public Map<Long, UserDto> getUsersByComments(Collection<EventComment> comments) {
        Set<Long> eventCommentAuthorList = comments
                .stream()
                .map(EventComment::getAuthorId)
                .collect(Collectors.toSet());
        return client.getUsers(eventCommentAuthorList.stream().toList()).stream()
                .collect(Collectors.toMap(UserDto::id, Function.identity()));
    }
}
