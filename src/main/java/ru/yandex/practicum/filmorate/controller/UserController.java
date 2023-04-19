package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController

public class UserController {

    private final Map<Integer, User> userMap = new HashMap<>();
    private int userIdCounter = 0;

    @ResponseBody
    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user) {
        log.info("Получен POST-запрос к эндпоинту: '/users' на добавление пользователя с ID={}", userIdCounter + 1);
        if (isValidUser(user)) {
            user.setId(userIdCounter + 1);
            userMap.put(user.getId(), user);

            return user;
        } else {
            throw new ValidationException("Пользователь не прошел валидацию");
        }
    }

    @ResponseBody
    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        log.info("Получен PUT-запрос к эндпоинту: '/users' на обновление пользователя с ID={}", user.getId());
        if (isValidUser(user)) {
            if (userMap.containsKey(user.getId())) {
                userMap.put(user.getId(), user);
                userIdCounter++;
                return user;
            } else {
                throw new ValidationException("Пользователь с ID=" + user.getId() + " не найден");
            }
        } else {
            throw new ValidationException("Пользователь не прошел валидацию");
        }
    }

    @GetMapping(value = "/users")
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    private boolean isValidUser(User user) {
        boolean isValid = true;
        if (!user.getEmail().contains("@")) {
            log.error("Некорректный e-mail пользователя: {}", user.getEmail());
            isValid = false;
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Некорректный логин пользователя: {}", user.getLogin());
            isValid = false;
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && LocalDate.parse(user.getBirthday().toString(), DateTimeFormatter.ISO_DATE).isAfter(LocalDate.now())) {
            log.error("Некорректная дата рождения пользователя: {}", user.getBirthday());
            isValid = false;
        }
        return isValid;
    }
}