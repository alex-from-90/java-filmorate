package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collections;
import java.util.List;

@Primary
@Component
@Slf4j
@RequiredArgsConstructor
public class FriendStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user != null && friend != null) {
            //@formatter:off
            String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, ?)";
            //@formatter:on
            jdbcTemplate.update(sql, userId, friendId, true);
        } else {
            // Один или оба пользователей не найдены
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Один или оба пользователя не найдены");
        }
    }

    public void deleteFriend(Long userId, Long friendId) {
        //@formatter:off
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        //@formatter:on
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user != null) {
            //@formatter:off
            String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday FROM users u "
                    + "JOIN friends f ON f.friend_id = u.id WHERE f.user_id = ?";
            //@formatter:on
            UserMapper userMapper = new UserMapper();
            return jdbcTemplate.query(sql, ps -> ps.setLong(1, userId), userMapper);
        } else {
            return Collections.emptyList();
        }
    }

    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        //@formatter:off
        String sql = "SELECT u.id, u.email, u.login, u.name, u.birthday "
                + "FROM friends f1 "
                + "JOIN friends f2 ON f1.friend_id = f2.friend_id "
                + "JOIN users u ON f1.friend_id = u.id "
                + "WHERE f1.user_id = ? AND f2.user_id = ?";
        //@formatter:on
        UserMapper userMapper = new UserMapper();
        return jdbcTemplate.query(sql, ps -> {
            ps.setLong(1, firstUserId);
            ps.setLong(2, secondUserId);
        }, userMapper);
    }
}