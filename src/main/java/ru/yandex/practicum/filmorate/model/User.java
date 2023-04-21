package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;

    @NotNull(message = "Пустая электронная почта")
    @NotBlank(message = "Имя не может быть пустым")
    @Email(message = "Некорректная почта")
    private String email;

    @NotNull(message = "Пустой логин")
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "В логине не могут находиться пробелы")
    private String login;

    private String name;

    @NotNull(message = "Пустая дата рождения")
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}