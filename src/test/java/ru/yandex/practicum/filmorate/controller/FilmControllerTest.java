package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmControllerTest {
    private Film film;
    private static Validator validator;
    private Set<ConstraintViolation<Film>> violations;

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        Set<Long> likes = new HashSet<>(Arrays.asList(1L, 2L, 3L));
        film = new Film(1L, "Кин-дза-дза!", "Бригадир Владимир Николаевич Машков и не подозревал, " +
                "что обычный путь в гастроном за хлебом и макаронами превратится в межгалактическое путешествие.",
                135, LocalDate.of(1986, 12, 1), likes);
        violations = validator.validate(film);
    }

    // проверка добавления фильма
    @Test
    public void testAddFilm() {
        violations = validator.validate(film);
        assertEquals(0, violations.size(), "Ошибки валидации");
        List<Film> films = new ArrayList<>();
        films.add(film);
        assertTrue(films.contains(film), "Фильм не был добавлен в список");
    }

    // проверка  при пустом названии у фильма
    @Test
    public void testEmptyFilmName() {
        film.setName("");
        violations = validator.validate(film);
        assertEquals(1, violations.size(), "Ошибки валидации");
    }

    // продолжительность фильма равна нулю
    @Test
    public void testInvalidDuration() {
        film.setDuration(-1);
        violations = validator.validate(film);
        assertEquals(1, violations.size(), "Ошибки валидации");
    }

    @Test
    public void testIncorrectRelease() {
        film.setReleaseDate(LocalDate.parse(String.valueOf(LocalDate.of(1895, 12, 27))));
        violations = validator.validate(film);
        assertEquals(1, violations.size(), "Ошибки валидации");
    }

    @Test
    public void testEmptyDescription() {
        film.setDescription("");
        violations = validator.validate(film);
        assertEquals(0, violations.size(), "Ошибки валидации");
    }

    // неправильная продолжительность фильма
    @Test
    public void testIncorrectDurationIsNegative() {
        film.setDuration(-1);
        violations = validator.validate(film);
        assertEquals(1, violations.size(), "Ошибки валидации");
    }
}
