package ru.practicum.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EventDateConstraintOrNullValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventDateConstraintOrNull {
    String message() default "Date must be in future +2 hours.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
