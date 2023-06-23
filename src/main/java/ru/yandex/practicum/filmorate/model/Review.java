package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class Review {
    private Long reviewId;
    @NotBlank(message = "Отсутсвует текст отзыва.")
    private String content;
    @NotNull(message = "Отсутствует тип отзыва : положительный или негативный.")
    private Boolean isPositive;
    @NotNull(message = "Отсутсвует ID пользователя, создавший отзыв.")
    private Long userId;
    @NotNull(message = "Отсутствует ID фильма, на который написан отзыв.")
    private Long filmId;
    private Long useful;
}
