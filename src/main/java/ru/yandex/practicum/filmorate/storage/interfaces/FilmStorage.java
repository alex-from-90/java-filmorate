package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.database.model.DirectorSortBy;
import ru.yandex.practicum.filmorate.storage.database.model.FilmSearchParameters;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {

    List<Film> getFilms();

    Film create(@Valid Film film);

    Film update(@Valid Film film);

    Film getFilmById(Long filmId);

    Film delete(Long filmId);

    List<Film> getRecommendations(Long id);

    List<Film> getDirectorFilms(int directorId, DirectorSortBy sortBy);

    List<Film> filmsSearch(String query, List<FilmSearchParameters> by);
}