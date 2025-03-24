package ru.practicum.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.enums.EventCommentStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentAdminRequest {
    @Size(min = 1, max = 7000)
    private String text;
    private EventCommentStatus status;
}
