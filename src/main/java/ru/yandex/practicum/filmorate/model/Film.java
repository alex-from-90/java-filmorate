package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.AfterDateFilm;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;

    @NotNull(message = "У фильма должно быть имя")
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotNull(message = "У фильма должно быть описание")
    @Size(max = 200, message = "Описание не должно больше 200 символов")
    private String description;

    @NotNull(message = "У фильма должна быть указана продолжительность")
    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    private Integer duration;

    @NotNull(message = "У фильма должна быть указана дата релиза")
    @AfterDateFilm(value = "1895-12-28", message = "Дата выхода должна быть после даты")
    private LocalDate releaseDate;

    Set<Long> likes = new HashSet<>();


    public Film(Long id, String name, String description, Integer duration, LocalDate releaseDate, Set<Long> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.releaseDate = releaseDate;
        if (likes == null) {
            this.likes = new HashSet<>();
        }
    }
}