package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.mapper.LikeMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Primary
@Component
@RequiredArgsConstructor
public class LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaService mpaService;
    private final GenreService genreService;

    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        String selectSql = "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(selectSql, Integer.class, filmId, userId);

        if (count != null && count > 0) {
            String deleteSql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
            jdbcTemplate.update(deleteSql, filmId, userId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Лайк для фильма с ID: " + filmId + " и User ID: " + userId);
        }
    }

    public List<Film> getPopular(long count) {
        String getPopularQuery = "SELECT films.*, rating_id " +
                "FROM films LEFT JOIN film_likes ON films.id = film_likes.film_id " +
                "GROUP BY films.id ORDER BY COUNT(film_likes.user_id) DESC LIMIT ?";


        return jdbcTemplate.query(getPopularQuery, (rs, rowNum) -> {
            Long filmId = rs.getLong("id");
            String filmName = rs.getString("name");
            String filmDescription = rs.getString("description");
            LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
            Integer duration = rs.getInt("duration");
            Mpa mpa = mpaService.getMpaById(rs.getInt("rating_id"));
            Set<Genre> genres = genreService.getFilmGenres(filmId);
            Set<Long> likes = new HashSet<>(getLikes(filmId));

            Film film = new Film();
            film.setId(filmId);
            film.setName(filmName);
            film.setDescription(filmDescription);
            film.setReleaseDate(releaseDate);
            film.setDuration(duration);
            film.setMpa(mpa);
            film.setGenres(genres);
            film.setLikes(likes);

            return film;
        }, count);
    }

    public List<Long> getLikes(Long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, new LikeMapper(), filmId);
    }
}