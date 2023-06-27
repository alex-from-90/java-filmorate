package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FeedMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
        Feed feed = new Feed();
        feed.setEventId(rs.getLong("event_id"));
        feed.setTimestamp(rs.getLong("timestamp"));
        feed.setUserId(rs.getLong("user_id"));
        feed.setEventType(rs.getString("event_type"));
        feed.setOperation(rs.getString("operation"));
        feed.setEntityId(rs.getLong("entity_id"));
        return feed;
    }
}
