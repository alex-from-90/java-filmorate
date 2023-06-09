package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.database.impl.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendDbStorage friendDbStorage;
    private final FilmStorage filmStorage;
    private final FeedService feedService;

    /**
     * Добавляет друга для пользователя с указанным ID и друга с указанным ID.
     */
    public void addFriend(Long userId, Long friendId) {
        friendDbStorage.addFriend(userId, friendId);
        feedService.createFeed(userId, friendId, "FRIEND", "ADD");
    }

    /**
     * Удаляет друга для пользователя с указанным ID и друга с указанным ID.
     */
    public void deleteFriend(Long userId, Long friendId) {
        friendDbStorage.deleteFriend(userId, friendId);
        feedService.createFeed(userId, friendId, "FRIEND", "REMOVE");
    }

    /**
     * Возвращает список друзей для пользователя с указанным ID.
     */
    public List<User> getFriends(Long userId) {
        return friendDbStorage.getFriends(userId);
    }

    /**
     * Возвращает список общих друзей для двух пользователей с указанными ID.
     */
    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        return friendDbStorage.getCommonFriends(firstUserId, secondUserId);
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

    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
    }

    public List<Film> getRecommendations(Long id) {
        return filmStorage.getRecommendations(id);
    }
}