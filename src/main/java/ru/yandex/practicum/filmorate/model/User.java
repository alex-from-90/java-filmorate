package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
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