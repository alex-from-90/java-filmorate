package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserStorage {
    User createUser(@Valid User user);

    User updateUser(@Valid User user);

    List<User> getAllUsers();

    User getUserById(Long userId);

    User delete(Long userId);
}