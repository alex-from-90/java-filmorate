package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.database.impl.GenreDbStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public Collection<Genre> getGenres() {
        return genreDbStorage.getGenres();
    }

    public Genre getGenreById(Integer id) {
        return genreDbStorage.getGenreById(id);
    }

    public Set<Genre> getFilmGenres(Long filmId) {
        return new HashSet<>(genreDbStorage.getFilmGenres(filmId));
    }
}