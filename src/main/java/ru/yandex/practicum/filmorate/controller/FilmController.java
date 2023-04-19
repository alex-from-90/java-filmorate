package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films;
    private Integer currentId;

    public FilmController() {
        currentId = 0;
        films = new HashMap<>();
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @ResponseBody
    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен POST-запрос к эндпоинту: '/films' на добавление фильма с ID={}", currentId);
        if (!isValidFilm(film)) {
            throw new ValidationException("Фильм не прошел валидацию");
        }
        film.setId(++currentId);
        films.put(film.getId(), film);
        return film;
    }

    @ResponseBody
    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен PUT-запрос к эндпоинту: '/films' на обновление фильма с ID={}", film.getId());
        if (isValidFilm(film)) {
            if (films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                return film;
            } else {
                throw new ValidationException("Фильм с ID=" + film.getId() + " не найден");
            }
        } else {
            throw new ValidationException("Фильм не прошел валидацию");
        }
    }

    private boolean isValidFilm(Film film) {
        if (film.getName().isEmpty()) {
            return false;
        }
        if ((film.getDescription().length()) > 200 || (film.getDescription().isEmpty())) {
            return false;
        }
         if (LocalDate.parse(film.getReleaseDate(), DateTimeFormatter.ISO_DATE).isBefore(LocalDate.of(1895, 12, 28))) {
            return false;
        }
        return film.getDuration() > 0;
    }
}