package ru.practicum.exceptions;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class Error {
    private HttpStatus status;
    private String reason;
    private String message;
    private String timestamp;
}