package ru.yandex.practicum.filmorate.storage.database.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FilmColumn {

    private Long id;
    private String name;
    private String description;
    private int duration;
    private Integer mpaId;
    private LocalDate releaseDate;
}