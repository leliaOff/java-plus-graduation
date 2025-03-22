package ru.practicum.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.UserDto;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/internal/users/{id}")
    UserDto getUser(@PathVariable Long id);

    @GetMapping("/internal/users/ids")
    List<UserDto> getUsers(@RequestParam List<Long> ids);
}