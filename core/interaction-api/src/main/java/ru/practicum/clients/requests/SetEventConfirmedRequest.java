package ru.practicum.clients.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SetEventConfirmedRequest {
    @NotNull
    private Integer confirmedRequests;
}
