package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeStorage likeStorage;


    public void addLike(Long filmId, Long userId) {
        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        likeStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopular(Integer count) {
        return likeStorage.getPopular(count);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Film delete(Long id) {
        return filmStorage.delete(id);
    }

    public List<Film> getDirectorFilms(int directorId, String sortBy) {
        return filmStorage.getDirectorFilms(directorId, sortBy);
    }
    public void addLike(Long filmId, Long userId) {
        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        likeStorage.deleteLike(filmId, userId);
    }
}