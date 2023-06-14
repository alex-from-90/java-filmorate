package ru.yandex.practicum.filmorate.storage.inMemory;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> userMap = new HashMap<>();
    private Long userIdCounter = 1L;

    @Override
    public User createUser(@Valid User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(userIdCounter++);
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(@Valid User user) {
        if (userMap.containsKey(user.getId())) {
            userMap.put(user.getId(), user);

            return user;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID=" + user.getId() + " не найден!");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User getUserById(Long userId) {
        if (!userMap.containsKey(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID=" + userId + " не найден!");
        }
        return userMap.get(userId);
    }

    @Override
    public User delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("Передан пустой ID!");
        }
        if (!userMap.containsKey(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID=" + userId + " не найден!");
        }
        // удаляем из списка друзей пользователя у других пользователей
        for (User user : userMap.values()) {
            user.getFriends().remove(userId);
        }
        return userMap.remove(userId);
    }
}
