package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;

@Primary
@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    UserMapper userMapper = new UserMapper();

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userMapper);
    }

    @Override
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        user.setId(simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue());

        log.info("Добавлен новый пользователь с ID={}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (getUserById(user.getId()) != null) {
            String sqlQuery = "UPDATE users SET " +
                    "email = ?, login = ?, name = ?, birthday = ? " +
                    "WHERE id = ?";
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            log.info("Пользователь с ID={} успешно обновлен", user.getId());
            return user;
        } else {
            throw new NotFoundException("Пользователь с ID=" + user.getId() + " не найден!");
        }
    }

    @Override
    public User getUserById(Long userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sql, userMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID=" + userId + " не найден!");
        }

        return user;
    }

    @Override
    public User delete(Long userId) {
        User user = getUserById(userId);
        String sqlQuery = "DELETE FROM users WHERE id = ? ";
        if (jdbcTemplate.update(sqlQuery, userId) == 0) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
        return user;
    }
}