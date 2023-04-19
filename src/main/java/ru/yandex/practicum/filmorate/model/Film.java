package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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