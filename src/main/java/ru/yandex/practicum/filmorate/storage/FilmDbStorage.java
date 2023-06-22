package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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
    public List<Film> filmsSearch(String query, String by) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        for (String s : by.split(",")) {
            params.addValue(s, query + "%");
        }

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<FilmColumn> filmColumns;
        if (params.getValues().size() == 2) {
            filmColumns = namedParameterJdbcTemplate.query(
                    "SELECT  FILMS.* FROM FILMS, FILM_LIKES As fl WHERE FILMS.ID IN (SELECT FILM_ID" +
                            " FROM FILM_DIRECTOR LEFT JOIN DIRECTORS D on D.ID = FILM_DIRECTOR.DIRECTOR_ID" +
                            "  WHERE D.NAME LIKE :director) OR  FILMS.NAME LIKE :title GROUP BY fl.FILM_ID" +
                            " ORDER BY COUNT(fl.FILM_ID);",
                    params,
                    new FilmMapper()
            );
        } else {
            filmColumns = namedParameterJdbcTemplate.query(
                    params.getValues().containsKey("title") ? "SELECT  FILMS.* FROM FILMS, FILM_LIKES As fl" +
                            " WHERE FILMS.NAME LIKE %title GROUP BY fl.FILM_ID ORDER BY COUNT(fl.FILM_ID)" :
                            "SELECT  FILMS.* FROM FILMS, FILM_LIKES As fl WHERE FILMS.ID IN (SELECT FILM_ID" +
                            " FROM FILM_DIRECTOR LEFT JOIN DIRECTORS D on D.ID = FILM_DIRECTOR.DIRECTOR_ID" +
                                    " WHERE D.NAME LIKE :director) ORDER BY COUNT(fl.FILM_ID);",
                    params,
                    new FilmMapper()
            );
        }
        return filmColumns.stream().map(this::fromColumnsToDto).collect(Collectors.toList());
        /*List<FilmColumn> filmColumns = namedParameterJdbcTemplate.query(
                params.getValues().size() == 2 ?
                        getDirectorSql + " OR  FILMS.NAME LIKE :title GROUP BY fl.FILM_ID" +
                                " ORDER BY COUNT(fl.FILM_ID);" :
                        params.getValues().containsKey("title") ?
                                "SELECT  FILMS.* FROM FILMS, FILM_LIKES As fl WHERE FILMS.NAME LIKE %title GROUP BY fl.FILM_ID" +
                                        " ORDER BY COUNT(fl.FILM_ID);" :
                                getDirectorSql + "ORDER BY COUNT(fl.FILM_ID);",
                params,
                new FilmMapper()
        );
        return filmColumns.stream().map(this::fromColumnsToDto).collect(Collectors.toList());
        String director = "";
        String title = "";
        if (by.contains("director")) director = query;
        if (by.contains("title")) title = query;
        List<FilmColumn> filmColumns = jdbcTemplate.query(
                params.getValues().containsKey("director") ?
                "SELECT  FILMS.* FROM FILMS, FILM_LIKES As fl WHERE FILMS.ID IN (SELECT FILM_ID" +
                        " FROM FILM_DIRECTOR LEFT JOIN DIRECTORS D on D.ID = FILM_DIRECTOR.DIRECTOR_ID" +
                        "  WHERE D.NAME LIKE '%' || ? || '%')OR  FILMS.NAME LIKE '%' || ? || '%' GROUP BY fl.FILM_ID" +
                        " ORDER BY COUNT(fl.FILM_ID);",
                new FilmMapper(),
                director,
                title
        );
        return filmColumns.stream().map(this::fromColumnsToDto).collect(Collectors.toList());*/
    }

    @Override
    public List<Film> getRecommendations(Long id) {
        String sql = "SELECT f.* FROM film_likes fl JOIN films f ON f.id = fl.film_id " +
                "WHERE fl.user_id = (SELECT t2.user_id " +
                "FROM film_likes t1 JOIN film_likes t2 ON t1.film_id = t2.film_id " +
                "AND t1.user_id != t2.user_id WHERE t1.user_id = ? GROUP BY t1.user_id, t2.user_id " +
                "ORDER BY count(*) DESC LIMIT 1) " +
                "AND fl.film_id NOT IN (SELECT film_id FROM film_likes WHERE user_id = ?)";
        List<FilmColumn> filmColumns = jdbcTemplate.query(sql, new FilmMapper(), id, id);
        List<Film> recommended = new ArrayList<>();
        for (FilmColumn filmColumn : filmColumns) {
            recommended.add(fromColumnsToDto(Objects.requireNonNull(filmColumn)));
        }
        return recommended;
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