package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Добавляет друга для пользователя с указанным ID и друга с указанным ID.
     */
    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    /**
     * Удаляет друга для пользователя с указанным ID и друга с указанным ID.
     */
    public void deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    /**
     * Возвращает список друзей для пользователя с указанным ID.
     */
    public List<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        List<User> friends = new ArrayList<>();
        if (!user.getFriends().isEmpty()) {
            for (Long currentId : user.getFriends()) {
                friends.add(userStorage.getUserById(currentId));
            }
        }
        return friends;
    }

    /**
     * Возвращает список общих друзей для двух пользователей с указанными ID.
     */
    public List<User> getCommonFriends(Long firstUserId, Long secondUserId) {
        User firstUser = userStorage.getUserById(firstUserId);
        User secondUser = userStorage.getUserById(secondUserId);
        if (firstUser == null || secondUser == null) {
            return new ArrayList<>(); // возвращает пустой список, если любой из пользователей равен null
        }
        Set<Long> intersection = new HashSet<>(firstUser.getFriends());
        intersection.retainAll(secondUser.getFriends());
        List<User> commonFriends = new ArrayList<>();
        for (Long i : intersection) {
            User friend = userStorage.getUserById(i);
            if (friend != null) { // существует ли этот друг, прежде чем добавлять его в список
                commonFriends.add(friend);
            }
        }
        return commonFriends;
    }
}


