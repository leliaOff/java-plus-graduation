package ru.practicum.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class EventDateConstraintOrNullValidator implements ConstraintValidator<EventDateConstraintOrNull, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime date, ConstraintValidatorContext context) {
        if (date == null) return true;
        else
            return date.isAfter(LocalDateTime.now().plusHours(2));
    }
}
