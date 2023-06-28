package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.model.DirectorSortBy;
import ru.yandex.practicum.filmorate.storage.model.FilmSearchParameters;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikeDbStorage likeDbStorage;
    private final FeedService feedService;

    public void addLike(Long filmId, Long userId) {
        likeDbStorage.addLike(filmId, userId);
        feedService.createFeed(userId, filmId, "LIKE", "ADD");
    }

    public void deleteLike(Long filmId, Long userId) {
        likeDbStorage.deleteLike(filmId, userId);
        feedService.createFeed(userId, filmId, "LIKE", "REMOVE");
    }

    public List<Film> getPopular(int count, int genreId, int year) {

        return likeDbStorage.getPopular(count, genreId, year);
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

    public List<Film> getDirectorFilms(int directorId, DirectorSortBy sortBy) {
        return filmStorage.getDirectorFilms(directorId, sortBy);
    }

    public List<Film> filmSearch(String query, List<FilmSearchParameters> by) {
        return filmStorage.filmsSearch(query, by);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        return likeDbStorage.getCommonFilms(userId, friendId);
    }
}