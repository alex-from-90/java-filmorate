package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {

    List<Feed> getFeedByUserId(long id);

    void addFeed(Feed feed);
}
