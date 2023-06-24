package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor

public class ReviewService {

    private final ReviewDbStorage reviewStorage;

    public void addLike(Long filmId, Long userId) {
        reviewStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        reviewStorage.deleteLike(filmId, userId);
    }

    //Добавить отзыв
    public Review add(Review review) {
        return reviewStorage.add(review);
    }

    //Обновить отзыв
    public Review updateReview(Review review) {
        return reviewStorage.update(review);  // Сделать обновление события
    }

    //Удалить отзыв
    public void deleteById(Long reviewId) {
        reviewStorage.deleteById(reviewId);
    }

    //Получить отзыв по ID
    public Review getReviewById(final Long id) {
        return reviewStorage.getById(id)
                .orElse(null);
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