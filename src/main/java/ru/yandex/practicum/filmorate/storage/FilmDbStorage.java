package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary // Наставник разрешил использовать вместо @Qualifier
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final MpaService mpaService;

    private final GenreService genreService;

    private final LikeStorage likeStorage;


    public List<Film> getFilms() {
        String sql = "SELECT id, name, description, release_date, duration, rating_id FROM films";
        FilmMapper filmMapper = new FilmMapper(mpaService, genreService, likeStorage);
        return jdbcTemplate.query(sql, filmMapper);
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("duration", film.getDuration());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("rating_id", film.getMpa().getId());
        long generatedId = simpleJdbcInsert.executeAndReturnKey(parameters).longValue();
        film.setId(generatedId);
        film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
        genreService.putGenres(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, " +
                "rating_id = ? WHERE id = ?";
        int updateCount = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (updateCount != 0) {
            film.setMpa(mpaService.getMpaById(film.getMpa().getId()));
            genreService.updateFilmGenres(film);

            return film;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден");
        }
    }

    @Override
    public Film getFilmById(Long filmId) {
        String sqlQuery = "SELECT id, name, description, release_date, duration, rating_id FROM films WHERE id = ?";
        FilmMapper filmMapper = new FilmMapper(mpaService, genreService, likeStorage);

        try {
            return jdbcTemplate.queryForObject(sqlQuery, filmMapper, filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден");
        }
    }

    @Override
    public Film delete(Long filmId) {
        Film film = getFilmById(filmId);
        String sqlQuery = "DELETE FROM films WHERE id = ? ";
        if (jdbcTemplate.update(sqlQuery, filmId) == 0) {
            throw new NotFoundException("Фильм с ID=" + filmId + " не найден!");
        }
        return film;
    }
}