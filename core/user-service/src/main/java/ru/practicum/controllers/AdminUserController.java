package ru.practicum.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.params.UserAdminParam;
import ru.practicum.services.UserService;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/users")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto addUser(@Valid @RequestBody NewUserRequest userRequest) {
        return userService.addUser(userRequest);
    }

    @GetMapping
    List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                           @RequestParam(defaultValue = "10") Integer size,
                           @RequestParam(defaultValue = "0") Integer from) {
        return userService.getUsers(new UserAdminParam(ids, size, from));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
