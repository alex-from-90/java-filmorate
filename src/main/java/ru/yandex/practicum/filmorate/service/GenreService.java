package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;

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

    public Set<Genre> getFilmGenres(Long filmId) {
        return new HashSet<>(genreStorage.getFilmGenres(filmId));
    }

    public Map<Integer, Genre> getFilmGenresAsMap(Long filmId) {
        List<Genre> list = genreStorage.getFilmGenres(filmId);
        HashMap<Integer, Genre> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            map.put(i, list[i]);
        }
        return new HashMap<>(genreStorage.getFilmGenres(filmId));
    }
}