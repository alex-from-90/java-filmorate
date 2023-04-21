package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.AfterDateFilm;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;

    @NotNull(message = "У фильма должно быть имя")
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotNull(message = "У фильма должно быть описание")
    @Size(max = 200, message = "Описание не должно больше 200 символов")
    private String description;

    @NotNull(message = "У фильма должна быть указана продолжительность")
    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    private  Integer duration;

    @NotNull(message = "У фильма должна быть указана дата релиза")
    @AfterDateFilm(value = "1895-12-28", message = "Дата выхода должна быть после даты")
    private LocalDate releaseDate;
}