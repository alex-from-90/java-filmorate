package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import javax.validation.Valid;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    //Добавление нового отзыва.
    @PostMapping
    public Review addReview( @Valid @RequestBody Review review ) {
        return reviewService.add(review);
    }

    //Редактирование уже имеющегося отзыва.
    @PutMapping
    public Review updateReview( @Valid @RequestBody Review review ) {
        return reviewService.update(review);
    }

    //Удаление уже имеющегося отзыва.
    @DeleteMapping("/{id}")
    public Review deleteReview(@PathVariable Long id ) {
        return reviewService.delete(id);
    }

    //Получение отзыва по идентификатору.
    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) {
        log.info("Request to get review by id = {}", id);
        return reviewService.getReviewById(id);
    }

    //Получение всех отзывов по идентификатору фильма, если фильм не указа но все. Если кол-во не указано то 10.
    @GetMapping
    public Collection<Review> findAll(@RequestParam(required = false) Long filmId,
                                      @RequestParam(defaultValue = "10", required = false) Long count) {
        log.info("Request to get reviews, filmId = {}, count = {}", filmId == null ? "all" : filmId, count);
        return reviewService.getAllReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен PUT-запрос к эндпоинту: '/films' на добавление лайка фильму с ID={}", id);
        reviewService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/films' на удаление лайка у фильма с ID={}", id);
        reviewService.deleteLike(id, userId);
    }

}
