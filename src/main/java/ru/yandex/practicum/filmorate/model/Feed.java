package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Feed {
    private Long feedId;
    private Long timestamp;
    private Long userId;
    private String eventType;
    private String operation;
    private Long entityId;
}
