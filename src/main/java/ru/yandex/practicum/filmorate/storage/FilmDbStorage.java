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
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.model.FilmColumn;

import java.util.*;
import java.util.stream.Collectors;

@Primary // Наставник разрешил использовать вместо @Qualifier
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage; // Поменяли сервисы на storage
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;


    public List<Film> getFilms() {
        String sql = "SELECT * FROM films";
        List<FilmColumn> filmColumns = jdbcTemplate.query(sql, new FilmMapper());

        return filmColumns.stream().map(this::fromColumnsToDto).collect(Collectors.toList());
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
        film.setMpa(mpaStorage.getMpaById(film.getMpa().getId())); //Напрямую, мимо сервисов
        genreStorage.add(film);

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
            film.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));
            genreStorage.updateGenres(film); //Напрямую, мимо сервисов

            return film;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден");
        }
    }

    @Override
    public Film getFilmById(Long filmId) {
        String sqlQuery = "SELECT * FROM films WHERE id = ?";
        try {
            FilmColumn filmColumn = jdbcTemplate.queryForObject(sqlQuery, new FilmMapper(), filmId);
            return fromColumnsToDto(Objects.requireNonNull(filmColumn));
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

    @Override
    public List<Film> getRecommendations(Long id) {
        String sql = "SELECT f.* FROM films f "
                + "JOIN film_likes fl ON f.id = fl.film_id WHERE user_id = ?";
        List<FilmColumn> mainUserFilms = jdbcTemplate.query(sql, new FilmMapper(), id);
        long reliedUserId = 0;
        long confidence = 0;
        long count = 0;
        for (FilmColumn mainUserFilm : mainUserFilms) {
            List<Long> likes = likeStorage.getLikes(mainUserFilm.getId());
            likes.remove(id);
            if (likes.isEmpty()) {
                continue;
            }
            for (Long userId: likes) {
                for (FilmColumn userFilm : jdbcTemplate.query(sql, new FilmMapper(), userId)) {
                    if (mainUserFilms.contains(userFilm)) {
                        count++;
                    }
                }
                if (count > confidence) {
                    confidence = count;
                    reliedUserId = userId;
                }
            }
        }
        List<Film> recommendation = new ArrayList<>();
        for (FilmColumn newFilm: jdbcTemplate.query(sql, new FilmMapper(), reliedUserId)) {
            if (!mainUserFilms.contains(newFilm)) {
                recommendation.add(fromColumnsToDto(Objects.requireNonNull(newFilm)));
            }
        }
        return recommendation;
    }

    private Film fromColumnsToDto(FilmColumn filmColumn) {
        Film film = new Film();
        film.setId(filmColumn.getId());
        film.setName(filmColumn.getName());
        film.setDescription(filmColumn.getDescription());
        film.setDuration(filmColumn.getDuration());
        film.setReleaseDate(filmColumn.getReleaseDate());
        film.setMpa(mpaStorage.getMpaById(filmColumn.getMpaId()));
        film.setGenres(new HashSet<>(genreStorage.getFilmGenres(film.getId())));
        film.setLikes(new HashSet<>(likeStorage.getLikes(filmColumn.getId())));
        return film;
    }
}