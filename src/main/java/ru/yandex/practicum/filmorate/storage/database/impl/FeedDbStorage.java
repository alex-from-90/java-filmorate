package ru.yandex.practicum.filmorate.storage.database.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.interfaces.FeedStorage;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Feed> getFeedByUserId(long id) {
        //@formatter:off
        String sql = "SELECT event_id, timestamp, user_id, event_type, operation, entity_id FROM "
                + "feeds "
                + "WHERE user_id = ?";
        //@formatter:on
        return jdbcTemplate.query(sql, new FeedMapper(), id);
    }

    public void addFeed(Feed feed) {

        if (feed.getUserId() > 0 && feed.getEntityId() > 0) {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("feeds")
                    .usingGeneratedKeyColumns("event_id");

            Map<String, Object> values = new HashMap<>();
            values.put("timestamp", feed.getTimestamp());
            values.put("user_id", feed.getUserId());
            values.put("event_type", feed.getEventType());
            values.put("operation", feed.getOperation());
            values.put("entity_id", feed.getEntityId());

            Long feedId = (Long) insert.executeAndReturnKey(values);

            feed.setEventId(feedId);
        }
    }

    public Feed createFeed(long userId, long entityId, String eventType, String operation) {

        Feed feed = new Feed();
        feed.setTimestamp(Instant.now().toEpochMilli());
        feed.setUserId(userId);
        feed.setEventType(eventType);
        feed.setOperation(operation);
        feed.setEntityId(entityId);

        addFeed(feed);

        return feed;
    }
}