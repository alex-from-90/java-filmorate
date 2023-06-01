package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Primary
@Component
@RequiredArgsConstructor
public class MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM ratings_mpa ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Mpa(
                rs.getInt("id"),
                rs.getString("name"))
        );
    }

    public Mpa getMpaById(Integer mpaId) {

        Mpa mpa;
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM ratings_mpa WHERE id = ?", mpaId);
        if (mpaRows.first()) {
            mpa = new Mpa(
                    mpaRows.getInt("id"),
                    mpaRows.getString("name")
            );
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Рейтинг с ID=" + mpaId + " не найден!");
        }
        return mpa;
    }
}