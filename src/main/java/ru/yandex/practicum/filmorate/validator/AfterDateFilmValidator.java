package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterDateFilmValidator implements ConstraintValidator<AfterDateFilm, LocalDate> {
    private LocalDate afterDate;

    @Override
    public void initialize(AfterDateFilm constraintAnnotation) {
        afterDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.isAfter(afterDate);
    }
}