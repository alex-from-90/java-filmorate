package ru.yandex.practicum.filmorate.storage.database.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Component
@RequiredArgsConstructor
public class DirectorDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorMapper directorMapper = new DirectorMapper();

    public List<Director> getDirectors() {
        String sql = "SELECT * FROM directors ORDER BY id";
        return jdbcTemplate.query(sql, directorMapper);
    }

    public Director getDirectorById(int directorId) {
        String sql = "select id, name from directors where id=?";

        try {
            return jdbcTemplate.queryForObject(sql, directorMapper, directorId);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Режиссер с ID=" + directorId + " не найден!");
        }
    }

    public void delete(int directorId) {
        jdbcTemplate.update("DELETE FROM films_directors WHERE director_id = ?", directorId);
        jdbcTemplate.update("DELETE FROM directors WHERE id = ?", directorId);
    }

    public Director createDirector(Director director) {
        if (director.getName().isBlank() || director.getName().isEmpty()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName(
                "directors").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", director.getName());
        int generatedId = simpleJdbcInsert.executeAndReturnKey(parameters).intValue();
        director.setId(generatedId);

        return director;
    }

    public void addDirectorToFilm(Film film) {
        String deleteQuery = "DELETE FROM films_directors WHERE film_id = ?";
        jdbcTemplate.update(deleteQuery, film.getId());

        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(
                        "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)",
                        film.getId(), director.getId());
            }
        }
    }

    public Collection<Director> getFilmDirectors(long filmId) {
        String sql = "SELECT * FROM films_directors"
                + " INNER JOIN directors ON director_id = id WHERE film_id = ?";
        return jdbcTemplate.query(sql, directorMapper, filmId);
    }

    public Director updateDirector(Director director) {
        String deleteGenresQuery = "UPDATE directors SET name = ? WHERE id = ?";
        int updatedDirector = jdbcTemplate.update(deleteGenresQuery, director.getName(),
                director.getId());
        if (updatedDirector == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Режиссер с ID = " + director.getId() + " не найден");
        }
        return director;
    }
}