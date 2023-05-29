package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.AfterDateFilm;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class Film {
    private Long id;

    @NotNull(message = "У фильма должно быть имя")
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotNull(message = "У фильма должно быть описание")
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull(message = "У фильма должна быть указана продолжительность")
    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    private Integer duration;

    @NotNull(message = "У фильма должна быть указана дата релиза")
    @AfterDateFilm(value = "1895-12-28", message = "Дата выхода должна быть после указанной даты")
    private LocalDate releaseDate;

    @NotNull(message = "У фильма должен быть указан рейтинг MPA и жанр ")
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();

    private Set<Long> likes = new HashSet<>();

    @JsonSetter
    public void setGenres(Set<Genre> genres) {
        this.genres = genres.stream()
                .sorted(Comparator.comparingInt(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}