package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public Collection<Genre> getGenres() {
        return genreStorage.getGenres();
    }

    public Genre getGenreById(Integer id) {
        return genreStorage.getGenreById(id);
    }

    public void putGenres(Film film) {
        genreStorage.delete(film);
        genreStorage.add(film);
    }

    public Set<Genre> getFilmGenres(Long filmId) {
        return new HashSet<>(genreStorage.getFilmGenres(filmId));
    }

    public void updateFilmGenres(Film film) {
        genreStorage.updateGenres(film);
    }
}