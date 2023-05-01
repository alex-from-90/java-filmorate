package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.endpoints.ApiEndpointsUser;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class UserController {


    private final UserStorage userStorage;
    private final UserService userService;

    @ResponseBody
    @PostMapping(ApiEndpointsUser.USERS)
    public User create(@Valid @RequestBody User user) {
        log.info("Получен POST-запрос к эндпоинту: '{}' на добавление пользователя", ApiEndpointsUser.USERS);
        return userStorage.createUser(user);
    }

    @ResponseBody
    @PutMapping(ApiEndpointsUser.USERS)
    public User update(@Valid @RequestBody User user) {
        log.info("Получен PUT-запрос к эндпоинту: '{}' на обновление пользователя с ID={}", ApiEndpointsUser.USERS, user.getId());
        return userStorage.updateUser(user);
    }

    @GetMapping(ApiEndpointsUser.USERS)
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @GetMapping(ApiEndpointsUser.USERS_ID)
    public User getUserById(@PathVariable Long id) {
        return userStorage.getUserById(id);
    }

    @GetMapping(ApiEndpointsUser.USERS_ID_FRIENDS)
    public List<User> getFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping(ApiEndpointsUser.USERS_ID_FRIENDS_COMMON)
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping(ApiEndpointsUser.USERS_ID_FRIENDS_FRIENDID)
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(ApiEndpointsUser.USERS_ID_FRIENDS_FRIENDID)
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriend(id, friendId);
    }
}






