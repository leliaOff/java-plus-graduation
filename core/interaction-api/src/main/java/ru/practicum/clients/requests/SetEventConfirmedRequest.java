package ru.practicum.clients.requests;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SetEventConfirmedRequest {
    @NotNull
    private Integer confirmedRequests;
}
