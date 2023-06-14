package ru.yandex.practicum.filmorate.storage.inMemory;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films;
    private Long currentId;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
        currentId = 0L;
    }

    @Override
    public List<Film> getFilms() {

        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(@Valid Film film) {
        film.setId(++currentId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(@Valid Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID=" + film.getId() + " не найден!");
        }
    }

    @Override
    public Film getFilmById(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID=" + filmId + " не найден!");
        }
        return films.get(filmId);
    }

    @Override
    public Film delete(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("Передан пустой ID!");
        }
        if (!films.containsKey(filmId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID=" + filmId + " не найден!");
        }
        return films.remove(filmId);
    }
}