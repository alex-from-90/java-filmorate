package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest

public class FilmControllerTest {
    private Film film;
    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        film = Film.builder()
                .name("Кин-дза-дза!")
                .description("Бригадир Владимир Николаевич Машков и не подозревал, " +
                        "что обычный путь в гастроном за хлебом и макаронами превратится в межгалактическое путешествие.")
                .releaseDate(String.valueOf(LocalDate.of(1986, 12, 1)))
                .duration(135)
                .id(1).build();
    }

    // проверка добавления фильма
    @Test
    public void testAddFilm() {
        Film film1 = filmController.create(film);
        assertEquals(film, film1, "Неверный ответ при передаче фильма");
        assertEquals(1, filmController.getFilms().size(), "Неверное колличество фильмов");
    }

    // проверка  при пустом названии у фильма
    @Test
    public void testEmptyFilmName() {
        film.setName("");
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    // проверка длины описания фильма
    @Test
    public void testNameLength() {
        film.setDescription(film.getDescription() + film.getDescription());
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    // проверка фильма без описания
    @Test
    public void testEmptyDescription() {
        film.setDescription("");
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    // проверка даты релиза
    @Test
    public void testIncorrectRelease() {
        film.setReleaseDate(String.valueOf(LocalDate.of(1895,12,27)));
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    // продолжительность фильма равна нулю
    @Test
    public void testIncorrectDurationNull() {
        film.setDuration(0);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    // неправильная продолжительность фильма
    @Test
    public void testIncorrectDurationIsNegative() {
        film.setDuration(-1);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }
}