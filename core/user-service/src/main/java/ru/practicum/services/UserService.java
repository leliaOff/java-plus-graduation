package ru.practicum.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.UserMapper;
import ru.practicum.models.User;
import ru.practicum.params.UserAdminParam;
import ru.practicum.repositories.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto addUser(NewUserRequest userRequest) {
        User user = userRepository.save(UserMapper.toModel(userRequest));
        return UserMapper.toDto(user);
    }

    public List<UserDto> getUsers(UserAdminParam param) {
        return CollectionUtils.isEmpty(param.getIds()) ? UserMapper.toDto(
                userRepository.findAll(PageRequest.of(param.getFrom(), param.getSize())).toList())
                : UserMapper.toDto(userRepository.findAllById(param.getIds()));
    }

    public List<UserDto> getUsers(List<Long> ids) {
        return UserMapper.toDto(userRepository.findAllById(ids));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
        userRepository.deleteById(id);
    }

    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
        return UserMapper.toDto(user);
    }
}
