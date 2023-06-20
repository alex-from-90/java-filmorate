package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor

public class ReviewService {
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewDbStorage reviewStorage;

    public void addLike(Long filmId, Long userId) {
        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        likeStorage.deleteLike(filmId, userId);
    }


    public Review add(Review review) {
        return reviewStorage.add(review);
    }

    public Review update(Review review) {
        return null;
    }

    public Review delete(Long id) {
        return null;
    }

    public Review getReviewById(final Long id) {
        return reviewStorage.getById(id).orElse(null);
    }

    public Collection<Review> getAllReviews(Long filmId, Long count) {
        return reviewStorage.getAll(filmId, count);
    }
}
