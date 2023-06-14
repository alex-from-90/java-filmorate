package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data

public class User {
    private Long id;

    @NotBlank(message = "Пустая электронная почта")
    @Email(message = "Некорректная почта")
    private String email;

    @NotBlank(message = "Пустой логин")
    @Pattern(regexp = "\\S+", message = "В логине не могут находиться пробелы")
    private String login;

    private String name;

    @NotNull(message = "Пустая дата рождения")
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();
}