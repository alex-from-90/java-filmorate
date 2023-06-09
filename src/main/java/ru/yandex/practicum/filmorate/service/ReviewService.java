package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.database.impl.ReviewDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewDbStorage reviewStorage;

    private final FeedService feedService;

    public void addLike(Long filmId, Long userId) {
        reviewStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        reviewStorage.deleteLike(filmId, userId);
    }

    //Добавить отзыв
    public Review add(Review review) {
        Review addedReview = reviewStorage.add(review);

        feedService.createFeed(addedReview.getUserId(), addedReview.getReviewId(), "REVIEW", "ADD");
        return addedReview;
    }

    //Обновить отзыв
    public Review updateReview(Review review) {
        Review addedReview = reviewStorage.update(review);

        feedService.createFeed(addedReview.getUserId(), addedReview.getReviewId(), "REVIEW",
                "UPDATE");

        return addedReview;
    }

    //Удалить отзыв
    public void deleteById(Long reviewId) {
        Review review = getReviewById(reviewId);

        feedService.createFeed(review.getUserId(), review.getReviewId(), "REVIEW", "REMOVE");

        reviewStorage.deleteById(reviewId);
    }

    //Получить отзыв по ID
    public Review getReviewById(final Long id) {
        return reviewStorage.getById(id).orElse(null);
    }

    //Получить все отзывы
    public Collection<Review> getAllReviews(Long filmId, Long count) {
        return reviewStorage.getAll(filmId, count);
    }

    //Поставить дизлайк
    public void addUserDislike(Long id, Long userId) {
        reviewStorage.addDislike(id, userId);
    }

    //Удалить дизлайк
    public void deleteUserDislike(Long id, Long userId) {
        reviewStorage.deleteDislike(id, userId);
    }
}