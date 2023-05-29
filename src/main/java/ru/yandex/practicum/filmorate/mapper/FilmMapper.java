package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class FilmMapper implements RowMapper<Film> {

    private final MpaService mpaService;
    private final GenreService genreService;
    private final LikeStorage likeStorage;

    public FilmMapper(MpaService mpaService, GenreService genreService, LikeStorage likeStorage) {
        this.mpaService = mpaService;
        this.genreService = genreService;
        this.likeStorage = likeStorage;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        film.setReleaseDate(rs.getDate("release_Date").toLocalDate());
        film.setMpa(mpaService.getMpaById(rs.getInt("rating_id")));
        film.setGenres(genreService.getFilmGenres(rs.getLong("id")));
        film.setLikes(new HashSet<>(likeStorage.getLikes(rs.getLong("id"))));

        return film;
    }
}