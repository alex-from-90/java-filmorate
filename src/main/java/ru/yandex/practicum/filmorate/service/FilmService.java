package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmService {
    private final Map<Integer, Film> films;
    private Integer currentId;

    public FilmService() {
        currentId = 0;
        films = new HashMap<>();
    }

    public List<Film> getFilms() {

        return new ArrayList<>(films.values());
    }

    public Film create(@Valid Film film) {
        film.setId(++currentId);
        films.put(film.getId(), film);
        return film;
    }

    public Film update(@Valid Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("Фильм с ID=" + film.getId() + " не найден");
        }
    }
}