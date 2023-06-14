package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.storage.model.FilmColumn;

import java.sql.ResultSet;
import java.sql.SQLException;

    public class FilmMapper implements RowMapper<FilmColumn> {
        //Убрали все экземляры
        @Override
        public FilmColumn mapRow(ResultSet rs, int rowNum) throws SQLException {
            FilmColumn film = new FilmColumn();
            film.setId(rs.getLong("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setDuration(rs.getInt("duration"));
            film.setMpaId(rs.getInt("rating_id"));
            film.setReleaseDate(rs.getDate("release_Date").toLocalDate());
            // Оставили только поля, обозначенные в Бд для таблицы фильма
            return film;
        }
    }
