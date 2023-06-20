package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.interfaces.FeedStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    public List<Feed> getFeedByUserId(long id) {

        String sql = "SELECT * FROM feeds WHERE user_id = ?";

        return jdbcTemplate.query(sql, new FeedMapper(), id);
    }

    public Feed addFeed(Feed feed) {

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("feeds")
                .usingGeneratedKeyColumns("feed_id");

        Map<String, Object> values = new HashMap<>();
        values.put("timestamp", feed.getTimestamp());
        values.put("user_id", feed.getUserId());
        values.put("event_type", feed.getEventType());
        values.put("operation", feed.getOperation());
        values.put("entity_id", feed.getEntityId());

        Long feedId = (Long) insert.executeAndReturnKey(values);

        feed.setFeedId(feedId);

        return feed;
    }
}
