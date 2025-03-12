package ru.practicum.controllers.admin;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.services.UserService;
import ru.practicum.user.UserAdminParam;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
public class UserAdminController {

    private final UserService userService;

    @Autowired
    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserDto addUser(@Valid @RequestBody NewUserRequest userRequest) {
        return userService.addUser(userRequest);
    }

    @GetMapping
    List<UserDto> getUser(@RequestParam(required = false) List<Long> ids,
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
