package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Film {
    private int id;

    @NotBlank(message = "Поле 'название или описание' должно быть заполнено")
    private String name;
    private String description;

    @NotNull(message = "Поле 'дата выпуска или продолжительность' должно быть заполнено")
    private String releaseDate;
    private  Integer duration;

}