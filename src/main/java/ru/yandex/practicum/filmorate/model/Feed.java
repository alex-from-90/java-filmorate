package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Feed {
    private Long eventId;
    private Long timestamp;
    private Long userId;
    private String eventType;
    private String operation;
    private Long entityId;
}
