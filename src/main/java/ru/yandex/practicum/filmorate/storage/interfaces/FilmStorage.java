package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {

    List<Film> getFilms();

    Film create(@Valid Film film);

    Film update(@Valid Film film);

    Film getFilmById(Long filmId);

    Film delete(Long filmId);
}