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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Primary
@Component
@Slf4j
@RequiredArgsConstructor
public class LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final UserStorage userService;

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Лайк для фильма с ID: " + filmId + " и User ID: " + userId);
        }
    }

    public List<Film> getPopular(long count) {
        //@formatter:off
        String getPopularQuery = "SELECT films.*, rating_id "
                + "FROM films LEFT JOIN film_likes ON films.id = film_likes.film_id "
                + "GROUP BY films.id ORDER BY COUNT(film_likes.user_id) DESC LIMIT ?";
        //@formatter:on

    public List<Film> getPopular(int count, int genreId, int year) {
        System.out.println(genreId);
        System.out.println(year);
        String sql = "";
        List<Film> films = new ArrayList<>();
        if (genreId == -1 && year == -1) {
            log.info("Filtering populars films no parameters");
            sql = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID , "
                    + "COUNT(L.USER_ID) as RATING FROM FILMS "
                    + "LEFT JOIN FILM_LIKES L on FILMS.ID = L.FILM_ID " + "GROUP BY FILMS" + ".ID "
                    + "ORDER BY RATING DESC LIMIT ?";
            films = jdbcTemplate.query(sql, (rs, rowNum) -> {
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

                return film;
            }, count);
        }
        if (genreId > 0 && year == -1) {
            log.info("Filtering populars films by genre");
            sql = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID, "
                    + "COUNT(L.USER_ID) as RATING FROM FILMS "
                    + "LEFT JOIN FILM_LIKES L on FILMS.ID = L.FILM_ID "
                    + "LEFT JOIN FILM_GENRES F on FILMS.ID = F.FILM_ID " + "WHERE F.GENRE_ID=?"
                    + " GROUP BY FILMS.ID,  F.GENRE_ID " + "ORDER BY RATING DESC LIMIT ?";
            films = jdbcTemplate.query(sql, (rs, rowNum) -> {
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
                log.info(film.toString());
                return film;
            }, genreId, count);
        }
        if (genreId == -1 && year > 0) {
            log.info("Filtering populars films by year");
            sql = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID , "
                    + "COUNT(L.USER_ID) as RATING FROM FILMS "
                    + "LEFT JOIN FILM_LIKES L on FILMS.ID = L.FILM_ID "
                    + "WHERE EXTRACT(YEAR FROM RELEASE_DATE)=?" + " GROUP BY FILMS.ID"
                    + " ORDER BY RATING DESC LIMIT ?";
            films = jdbcTemplate.query(sql, (rs, rowNum) -> {
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

                return film;
            }, year, count);
        }
        if (genreId > 0 && year > 0) {
            log.info("Filtering populars films by genre and year");
            sql = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID , "
                    + "COUNT(L.USER_ID) as RATING FROM FILMS "
                    + "LEFT JOIN FILM_LIKES L on FILMS.ID = L.FILM_ID "
                    + "LEFT JOIN FILM_GENRES F on FILMS.ID = F.FILM_ID " + "WHERE F.GENRE_ID=?"
                    + " AND EXTRACT(YEAR FROM RELEASE_DATE)=?" + " GROUP BY FILMS.ID,  F.GENRE_ID "
                    + "ORDER BY RATING DESC LIMIT ?";
            films = jdbcTemplate.query(sql, (rs, rowNum) -> {
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

                return film;
            }, genreId, year, count);
        }
        if (genreId < -1 && year < -1) {
            throw new ValidationException(String.format(
                    "Incorrect parameters for filtering populars - films"
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
            throw new NotFoundException("User not " + "found");
        if (userService.getUserById(friendId) == null)
            throw new NotFoundException("User not " + "found");
        List<Film> films;
        String sql = "SELECT *" + "FROM films AS f "
                + "LEFT JOIN (SELECT film_id, COUNT(film_id) AS count_like FROM film_likes GROUP BY film_id) "
                + "ON (f.ID = film_Id)" + "RIGHT JOIN film_likes AS l1 ON f.id = l1.film_id "
                + "RIGHT JOIN film_likes AS l2 ON l1.film_id = l2.film_id "
                + "WHERE l1.user_id = ? AND l2.user_id = ? " + "ORDER BY count_like DESC;";

        films = jdbcTemplate.query(sql, (rs, rowNum) -> {
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

            return film;
        }, userId, friendId);

        return films;
    }
}