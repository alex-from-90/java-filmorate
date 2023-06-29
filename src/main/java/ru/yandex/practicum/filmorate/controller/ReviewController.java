package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("Получен POST запрос на добавление отзыва");
        return reviewService.add(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Получен PUT запрос на обновление отзыва");
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        log.info("Получен DELETE запрос на удаление отзыва с id = {}", id);
        reviewService.deleteById(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        log.info("Получен GET запрос на получение отзыва с id = {}", id);
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public Collection<Review> findAll(@RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10", required = false) Long count) {
        log.info("Получен GET запрос на получение всех отзывов , filmId = {}, count = {}",
                filmId == null ? "all" : filmId, count);
        return reviewService.getAllReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен PUT-запрос к эндпоинту: '/reviews' на добавление лайка фильму с ID={}",
                id);
        reviewService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/reviews' на удаление лайка у фильма с ID={}",
                id);
        reviewService.deleteLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addUserDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен PUT запрос на добавлени дизлайка id = {} от пользователя ID = {}", id,
                userId);
        reviewService.addUserDislike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteUserDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен DELETE запрос на удаление дизлайка id = {} от пользователя ID = {}", id,
                userId);
        reviewService.deleteUserDislike(id, userId);
    }
}