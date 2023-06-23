package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Positive;

@Data
public class Feed {
    private Long eventId;
    private Long timestamp;
    @Positive
    private Long userId;
    private String eventType;
    private String operation;
    @Positive
    private Long entityId;
}
