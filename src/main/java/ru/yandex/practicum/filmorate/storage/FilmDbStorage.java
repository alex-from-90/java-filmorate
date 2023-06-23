package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage; // Поменяли сервисы на storage
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final DirectorStorage directorStorage;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Film> getFilms() {
        //@formatter:off
        String sql = "SELECT * FROM films";
        //@formatter:on
        List<FilmColumn> filmColumns = jdbcTemplate.query(sql, new FilmMapper());
        return filmColumns.stream()
                .map(this::fromColumnsToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName(
                        "films")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("duration", film.getDuration());
        parameters.put("release_date", film.getReleaseDate());
        parameters.put("rating_id", film.getMpa()
                .getId());
        long generatedId = simpleJdbcInsert.executeAndReturnKey(parameters)
                .longValue();
        film.setId(generatedId);
        film.setMpa(mpaStorage.getMpaById(film.getMpa()
                .getId())); //Напрямую, мимо сервисов
        genreStorage.add(film);
        directorStorage.addDirectorToFilm(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        //@formatter:off
        String sqlQuery = "UPDATE films SET "
                + "name = ?, description = ?, release_date = ?, duration = ?, "
                + "rating_id = ? WHERE id = ?";
        //@formatter:on
        int updateCount = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa()
                        .getId(), film.getId());

        if (updateCount != 0) {
            film.setMpa(mpaStorage.getMpaById(film.getMpa()
                    .getId()));
            genreStorage.updateGenres(film); //Напрямую, мимо сервисов
            directorStorage.addDirectorToFilm(film);

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
            params.addValue(s, "%" + query + "%");
        }

        String sqlFilm = " ";
        if (params.getValues().size() == 2) sqlFilm = "OR FILMS.NAME ILIKE :title";

        List<FilmColumn> filmColumns = namedParameterJdbcTemplate.query(
                "SELECT FILMS.*, COUNT(fl.FILM_ID) FROM FILMS LEFT JOIN FILM_LIKES fl on FILMS.ID = fl.FILM_ID " +
                        (params.getValues().containsKey("director") ?
                                " WHERE FILMS.ID IN (SELECT FILM_ID FROM FILMS_DIRECTORS LEFT JOIN DIRECTORS D on D.ID = " +
                                        " FILMS_DIRECTORS.DIRECTOR_ID WHERE D.NAME ILIKE :director) " + sqlFilm :
                                " WHERE FILMS.NAME ILIKE :title") +
                        " GROUP BY fl.FILM_ID ORDER BY COUNT(fl.FILM_ID) desc",
                    params,
                    new FilmMapper()
            );
        return filmColumns.stream()
                .map(this::fromColumnsToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getRecommendations(Long id) {
        String sql = "SELECT f.* FROM film_likes fl JOIN films f ON f.id = fl.film_id "
                + "WHERE fl.user_id = (SELECT t2.user_id "
                + "FROM film_likes t1 JOIN film_likes t2 ON t1.film_id = t2.film_id "
                + "AND t1.user_id != t2.user_id WHERE t1.user_id = ? GROUP BY t1.user_id, t2.user_id "
                + "ORDER BY count(*) DESC LIMIT 1) "
                + "AND fl.film_id NOT IN (SELECT film_id FROM film_likes WHERE user_id = ?)";
        List<FilmColumn> filmColumns = jdbcTemplate.query(sql, new FilmMapper(), id, id);
        List<Film> recommended = new ArrayList<>();
        for (FilmColumn filmColumn : filmColumns) {
            recommended.add(fromColumnsToDto(Objects.requireNonNull(filmColumn)));
        }
        return recommended;
    }

    @Override
    public List<Film> getDirectorFilms(int directorId, String sortBy) {
        String sql = "SELECT * FROM films " +
                "INNER JOIN films_directors ON film_id = id WHERE director_id = ?";

        List<FilmColumn> filmColumns = jdbcTemplate.query(sql, new FilmMapper(), directorId);
        if (filmColumns.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Режиссер с ID=" + directorId + " не найден!");
        }

        List<Film> directorFilms = filmColumns.stream().map(this::fromColumnsToDto).collect(Collectors.toList());
        switch (sortBy) {
            case "likes":
                return directorFilms.stream()
                        .sorted(Comparator.comparingInt(o -> o.getLikes().size()))
                        .collect(Collectors.toList());
            case "year":
                return directorFilms.stream()
                        .sorted(Comparator.comparingInt(o -> o.getReleaseDate().getYear()))
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
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
        film.setDirectors(new HashSet<>(directorStorage.getFilmDirectors(film.getId())));
        film.setLikes(new HashSet<>(likeStorage.getLikes(filmColumn.getId())));
        return film;
    }
}