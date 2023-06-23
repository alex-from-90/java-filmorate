package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class Feed {
    private Long eventId;
    @NotNull
    private Long timestamp;
    @Positive
    private Long userId;
    @NotNull
    private String eventType;
    @NotNull
    private String operation;
    @Positive
    private Long entityId;
}
