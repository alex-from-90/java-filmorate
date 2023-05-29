package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    /**
     * Добавляет друга для пользователя с указанным ID и друга с указанным ID.
     */
    public void addFriend(Long userId, Long friendId) {
        friendStorage.addFriend(userId, friendId);
    }

    /**
     * Удаляет друга для пользователя с указанным ID и друга с указанным ID.
     */
    public void deleteFriend(Long userId, Long friendId) {
        friendStorage.deleteFriend(userId, friendId);
    }

    /**
     * Возвращает список друзей для пользователя с указанным ID.
     */
    public List<User> getFriends(Long userId) {
        return friendStorage.getFriends(userId);
    }

    /**
     * Возвращает список общих друзей для двух пользователей с указанными ID.
     */
    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        return friendStorage.getCommonFriends(firstUserId, secondUserId);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }
}