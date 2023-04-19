package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;

    @Email(message = "Введите корректный email")
    private String email;

    @NotBlank(message = "Поле 'Логин и Имя' должно быть заполнено")
    private String login;
    private String name;

    @PastOrPresent(message = "Поле 'Дата рождения' должно быть заполнено")
    private LocalDate birthday;
}