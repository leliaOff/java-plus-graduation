package ru.practicum.exceptions;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class Error {
    private HttpStatus status;
    private String reason;
    private String message;
    private String timestamp;
}