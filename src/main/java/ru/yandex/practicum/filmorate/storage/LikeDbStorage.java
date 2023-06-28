package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.LikeMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Primary
@Component
@Slf4j
@RequiredArgsConstructor
public class LikeDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final UserStorage userService;
    private final DirectorDbStorage directorDbStorage;

    public void addLike(Long filmId, Long userId) {
        String sql = "MERGE INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        String selectSql = "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(selectSql, Integer.class, filmId, userId);

        if (count != null && count > 0) {
            String deleteSql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
            jdbcTemplate.update(deleteSql, filmId, userId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Лайк для фильма с ID: " + filmId + " и User ID: " + userId);
        }
    }

    public List<Film> getPopular(int count, int genreId, int year) {
        String sql;
        List<Film> films = new ArrayList<>();
        if (genreId == -1 && year == -1) {
            log.info("Фильтрация популярных фильмов без параметров");
            sql = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID , "
                    + "COUNT(L.USER_ID) as RATING FROM FILMS "
                    + "LEFT JOIN FILM_LIKES L on FILMS.ID = L.FILM_ID "
                    + "GROUP BY FILMS" + ".ID "
                    + "ORDER BY RATING DESC LIMIT ?";
            films = jdbcTemplate.query(sql, (rs, rowNum) -> createCurrentFilm(rs), count);
        }
        if (genreId > 0 && year == -1) {
            log.info("Фильтрация популярных фильмов по жанрам");
            sql = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID, "
                    + "COUNT(L.USER_ID) as RATING FROM FILMS "
                    + "LEFT JOIN FILM_LIKES L on FILMS.ID = L.FILM_ID "
                    + "LEFT JOIN FILM_GENRES F on FILMS.ID = F.FILM_ID "
                    + "WHERE F.GENRE_ID=?"
                    + " GROUP BY FILMS.ID,  F.GENRE_ID "
                    + "ORDER BY RATING DESC LIMIT ?";
            films = jdbcTemplate.query(sql, (rs, rowNum) -> createCurrentFilm(rs), genreId, count);
        }
        if (genreId == -1 && year > 0) {
            log.info("Фильтрация популярных фильмов по годам");
            sql = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID , "
                    + "COUNT(L.USER_ID) as RATING FROM FILMS "
                    + "LEFT JOIN FILM_LIKES L on FILMS.ID = L.FILM_ID "
                    + "WHERE EXTRACT(YEAR FROM RELEASE_DATE)=?"
                    + " GROUP BY FILMS.ID"
                    + " ORDER BY RATING DESC LIMIT ?";
            films = jdbcTemplate.query(sql, (rs, rowNum) -> createCurrentFilm(rs), year, count);
        }
        if (genreId > 0 && year > 0) {
            log.info("Фильтрация популярных фильмов по жанрам и годам");
            sql = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID , "
                    + "COUNT(L.USER_ID) as RATING FROM FILMS "
                    + "LEFT JOIN FILM_LIKES L on FILMS.ID = L.FILM_ID "
                    + "LEFT JOIN FILM_GENRES F on FILMS.ID = F.FILM_ID "
                    + "WHERE F.GENRE_ID=?"
                    + " AND EXTRACT(YEAR FROM RELEASE_DATE)=?"
                    + " GROUP BY FILMS.ID,  F.GENRE_ID "
                    + "ORDER BY RATING DESC LIMIT ?";
            films = jdbcTemplate.query(sql, (rs, rowNum) ->
                    createCurrentFilm(rs), genreId, year, count);
        }
        if (genreId < -1 && year < -1) {
            throw new ValidationException(String.format(
                    "Неверные параметры фильтрации популярных фильмов"
                            + " genreid = %d and year = %d.", genreId, year));
        }

        return films;
    }

    public List<Long> getLikes(Long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.query(sql, new LikeMapper(), filmId);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        if (userService.getUserById(userId) == null)
            throw new NotFoundException("Друг пользователя не найден!");
        List<Film> films;
        String sql = "SELECT *" + "FROM films AS f "
                + "JOIN film_likes AS LIKES_FIRST_USER ON f.id = LIKES_FIRST_USER.film_id "
                + "JOIN film_likes AS LIKES_SECOND_USER ON LIKES_FIRST_USER.film_id = "
                + "LIKES_SECOND_USER.film_id "
                + "WHERE LIKES_FIRST_USER.user_id = ? AND LIKES_SECOND_USER.user_id = ? ";

        films = jdbcTemplate.query(sql, (rs, rowNum) -> createCurrentFilm(rs), userId, friendId);

        return films;
    }

    private Film createCurrentFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        Long filmId = rs.getLong("id");
        film.setId(filmId);
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date")
                .toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(mpaService.getMpaById(rs.getInt("rating_id")));
        film.setGenres(genreService.getFilmGenres(filmId));
        film.setLikes(new HashSet<>(getLikes(filmId)));
        Collection<Director> directors = directorDbStorage.getFilmDirectors(filmId);
        film.getDirectors().addAll(directors);

        return film;
    }
}
