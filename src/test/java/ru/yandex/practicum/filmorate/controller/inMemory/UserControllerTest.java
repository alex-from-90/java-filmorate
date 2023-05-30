package ru.yandex.practicum.filmorate.controller.inMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerTest {
    private User user;
    private static Validator validator;
    private Set<ConstraintViolation<User>> violations;

    @BeforeEach
    public void beforeEach() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        user = new User();
        user.setId(1L);
        user.setEmail("alex@site-n.ru");
        user.setLogin("n911");
        user.setName("Алексей");
        user.setBirthday(LocalDate.of(1980, 9, 13));

        violations = validator.validate(user);
    }

    // Добавление пользователя
    @Test
    public void testAddFilm() {
        violations = validator.validate(user);
        assertEquals(0, violations.size(), "Ошибки валидации");
        List<User> users = new ArrayList<>();
        users.add(user);
        assertTrue(users.contains(user), "Пользователь не был добавлен в список");
    }

    // проверка пустого email
    @Test
    public void testEmailIsEmpty() {
        user.setEmail("");
        violations = validator.validate(user);
        assertEquals(1, violations.size(), "Ошибки валидации");
    }

    // проверка неправильной почты
    @Test
    public void testIncorrectEmail() {
        user.setEmail("yandex.ru");
        violations = validator.validate(user);
        assertEquals(1, violations.size(), "Ошибки валидации");
    }

    // проверка пустого логина

    @Test
    public void testIncorrectLogin() {
        user.setLogin("");
        violations = validator.validate(user);
        assertEquals(2, violations.size(), "Ошибки валидации");
    }

    // проверка на пробелы в имени

    @Test
    public void testIncorrectNameWithSpaces() {
        user.setLogin("Леонид Куравлёв");
        violations = validator.validate(user);
        assertEquals(1, violations.size(), "Ошибки валидации");
    }

    @Test
    public void testEmptyName() {
        user.setName("");
        violations = validator.validate(user);
        assertEquals(0, violations.size(), "Ошибки валидации");
    }

    // Неправильная дата рождения

    @Test
    public void testFutureBirthday() {
        user.setBirthday(LocalDate.parse(String.valueOf(LocalDate.now().plusDays(1))));
        violations = validator.validate(user);
        assertEquals(1, violations.size(), "Ошибки валидации");
    }
}