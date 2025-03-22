package ru.practicum.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;

@UtilityClass
public class UserDtoMapper {
    public static UserShortDto toShortDto(UserDto userDto) {
        return new UserShortDto(
                userDto.id(),
                userDto.name()
        );
    }
}
