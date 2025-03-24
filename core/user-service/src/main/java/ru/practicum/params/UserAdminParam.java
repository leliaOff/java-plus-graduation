package ru.practicum.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserAdminParam {
    private List<Long> ids;
    private Integer size;
    private Integer from;
}