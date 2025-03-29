package ru.practicum.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.UserDto;
import ru.practicum.services.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/internal/users")
public class InternalUserController {

    private final UserService userService;

    @Autowired
    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/ids")
    List<UserDto> getUsers(@RequestParam List<Long> ids) {
        return userService.getUsers(ids);
    }
}
