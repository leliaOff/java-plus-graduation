package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserAdminParam {
    private List<Long> ids;
    private Integer size;
    private Integer from;
}