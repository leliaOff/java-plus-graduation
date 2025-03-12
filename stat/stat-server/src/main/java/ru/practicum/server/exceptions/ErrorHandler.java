package ru.practicum.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice("ru.practicum.server")
public class ErrorHandler {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleBadRequestException(final BadRequestException ex) {
        return Error.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("Incorrectly made request.")
                .message(ex.getMessage())
                .timestamp(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}