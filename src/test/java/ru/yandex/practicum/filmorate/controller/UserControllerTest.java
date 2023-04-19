package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {

    private User user;
    private UserController userController;
    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        user = User.builder()
                .name("Алексей")
                .login("n911")
                .email("alex@site-n.ru")
                .birthday(LocalDate.parse(String.valueOf(LocalDate.of(1980, 9, 13))))
                .id(1).build();
    }

    // Добавление пользователя
    @Test
    public void testAddUserAllCorrect() {
        User user1 = userController.create(user);
        assertEquals(user, user1, "Неверный отчёт при передачи пользователя");
        assertEquals(1, userController.getAllUsers().size(), "Ошибка списока пользователей");
    }

    // проверка пустого email
    @Test
    public void testEmailIsEmpty() {
        user.setEmail("");
        assertThrows(ValidationException.class, () -> userController.create(user));
        assertEquals(0, userController.getAllUsers().size(), "Ошибка списока пользователей");
    }

    // проверка неправильной почты
    @Test
    public void testIncorrectEmail() {
        user.setEmail("yandex.ru");
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    // проверка пустого логина
    @Test
    public void testIncorrectLogin() {
        user.setLogin("");
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    // проверка на пробелы в имени
    @Test
    public void testIncorrectNameWithSpaces() {
        user.setLogin("Леонид Куравлёв");
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    // проверка на пустое имя
    @Test
    public void testEmptyName() {
        user.setName("");
        User user1 = userController.create(user);
        assertEquals(user1.getName(), user.getLogin(), "Имя и логин пользователя должны совпадать");
    }

    // Неправильная дата рождения
    @Test
    public void testFutureBirthday() {
        user.setBirthday(LocalDate.parse(String.valueOf(LocalDate.now().plusDays(1))));
        assertThrows(ValidationException.class, () -> userController.create(user));
    }
}