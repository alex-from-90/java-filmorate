package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FeedStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedStorage feedStorage;
    private final UserStorage userStorage;

    public List<Feed> getFeedByUserId(long userId) {

        User user = userStorage.getUserById(userId);

        return feedStorage.getFeedByUserId(userId);
    }

    public void createFeed(long userId, long entityId, String eventType, String operation) {

            Feed feed = new Feed();
            feed.setTimestamp(Instant.now().toEpochMilli());
            feed.setUserId(userId);
            feed.setEventType(eventType);
            feed.setOperation(operation);
            feed.setEntityId(entityId);

            feedStorage.addFeed(feed);
    }
}