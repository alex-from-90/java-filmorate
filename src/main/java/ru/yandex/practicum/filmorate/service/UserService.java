package ru.yandex.practicum.filmorate.servise;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    private final Map<Integer, User> userMap = new HashMap<>();
    private int userIdCounter = 1;

    public User createUser(@Valid User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(userIdCounter++);
        userMap.put(user.getId(), user);
        return user;
    }

    public User updateUser(@Valid User user) {
        if (userMap.containsKey(user.getId())) {
            userMap.put(user.getId(), user);

            return user;
        } else {
            throw new ValidationException("Пользователь с ID=" + user.getId() + " не найден");
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }
}