package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD
import ru.yandex.practicum.filmorate.model.Feed;
=======
import ru.yandex.practicum.filmorate.model.Film;
>>>>>>> develop
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FeedService feedService;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен POST-запрос к эндпоинту: '/users' на добавление пользователя");
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен PUT-запрос к эндпоинту: '/users' на обновление пользователя с ID={}",
                user.getId());
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users' на удаление пользователя с ID={}",
                id);
        userService.deleteUserById(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен GET-запрос к эндпоинту: '/users' на вывод списка всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение пользователя с ID={}", id);
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение друзей с ID={}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info(
                "Получен GET-запрос к эндпоинту: '/users' на получение друзей пользователя с ID={}",
                id);
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getFeedByUserId(@PathVariable Long id) {
        log.info(
                "Получен GET-запрос к эндпоинту: '/users/id/feed' на получения списка событий "
                        + "пользователя с ID={}", id);
        return feedService.getFeedByUserId(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info(
                "Получен PUT-запрос к эндпоинту: '/users' на добавление в друзья пользователя с ID={}",
                id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users' удаление дружбы пользователя с ID={}",
                id);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Long id) {
        log.info(
                "Получен GET-запрос к эндпоинту: '/users' на получение рекоммендаций пользователю с ID={}",
                id);
        return userService.getRecommendations(id);
    }
}