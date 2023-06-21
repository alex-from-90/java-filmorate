package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Primary
@Component
@RequiredArgsConstructor
public class ReviewDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    //Отзывы
    public Review add(Review review) {
        review.setUseful(0L);

        User user = userDbStorage.getUserById(review.getUserId());
        if (user == null || filmDbStorage.getFilmById(review.getFilmId()) == null) {
            throw new NotFoundException(String.format("Неверный id (%d) при добавлении", review.getFilmId()));
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("REVIEWS")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("content", review.getContent());
        parameters.put("is_positive", review.getIsPositive());
        parameters.put("user_id", review.getUserId());
        parameters.put("film_id", review.getFilmId());
        parameters.put("useful", review.getUseful());

        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(parameters).longValue());

        return review;
    }

    public Collection<Review> getAll(Long filmId, Long count) {
        String getAllReviewsQuery;
        Object[] args;
        int[] argTypes;
        if (filmId == null) {
            getAllReviewsQuery = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            args = new Object[]{count};
            argTypes = new int[]{java.sql.Types.BIGINT};
        } else {
            getAllReviewsQuery = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            args = new Object[]{filmId, count};
            argTypes = new int[]{java.sql.Types.BIGINT, java.sql.Types.BIGINT};
        }
        return jdbcTemplate.query(getAllReviewsQuery, args, argTypes, new ReviewMapper());
    }

    public Optional<Review> getById(Long reviewId) {
        String getByIdQuery = "SELECT * FROM reviews WHERE review_id = ?";
        Object[] args = new Object[]{reviewId};
        int[] argTypes = new int[]{Types.BIGINT};

        Collection<Review> reviews = jdbcTemplate.query(getByIdQuery, args, argTypes, new ReviewMapper());
        if (!reviews.isEmpty()) {
            return reviews.stream().findFirst();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Review not found with id = %d", reviewId));
        }
    }

    public Review update(Review review) {
        Optional<Review> existingReview = getById(review.getReviewId());
        if (existingReview.isPresent()) {
            Review oldReview = existingReview.get();
            review.setUserId(oldReview.getUserId());
            review.setFilmId(oldReview.getFilmId());
            review.setUseful(oldReview.getUseful());

            String updateReviewByIdQuery = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
            jdbcTemplate.update(updateReviewByIdQuery,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getReviewId());

            return review;
        } else {
            throw new NotFoundException(String.format("Неверный id отзыва при обновлении. id = %d", review.getReviewId()));
        }
    }

    public void deleteById(Long reviewId) {
        String checkReviewExistsQuery = "SELECT COUNT(*) FROM reviews WHERE review_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkReviewExistsQuery, Integer.class, reviewId);

        if (count != null && count > 0) {
            String deleteReviewByIdQuery = "DELETE FROM REVIEWS WHERE review_id = ?";
            jdbcTemplate.update(deleteReviewByIdQuery, reviewId);
        } else {
            throw new NotFoundException(String.format("Неверный id при удалении " +
                    " id = %d", reviewId));
        }
    }

    //Лайки
    //Добавить лайк
    public void addLike(Long reviewId, Long userId) {
        deleteDislike(reviewId, userId); //При лайке удаляем дизлайк
        String addLikeQuery = "INSERT INTO review_like (review_id, user_id, IS_USEFUL) VALUES (?,?,true)";
        jdbcTemplate.update(addLikeQuery, reviewId, userId);
        jdbcTemplate.update("UPDATE REVIEWS SET useful = ? WHERE review_id = ?", countUseful(reviewId), reviewId);
    }

    //Удалить лайк
    public void deleteLike(Long reviewId, Long userId) {
        String deleteLikeQuery = "DELETE FROM review_like WHERE review_id = ? AND user_id = ? AND IS_USEFUL = true";
        jdbcTemplate.update(deleteLikeQuery, reviewId, userId);
        jdbcTemplate.update("UPDATE REVIEWS SET useful = ? WHERE review_id = ?", countUseful(reviewId), reviewId);
    }

    //Добавить дизлайк
    public void addDislike(Long reviewId, Long userId) {
        deleteLike(reviewId, userId); // При длизлайке удаляем лайк
        String addDislikeQuery = "INSERT INTO review_like (review_id, user_id, IS_USEFUL) VALUES (?,?,false)";
        jdbcTemplate.update(addDislikeQuery, reviewId, userId);
        jdbcTemplate.update("UPDATE REVIEWS SET useful = ? WHERE review_id = ?", countUseful(reviewId), reviewId);
    }

    //Удалить дизлайк
    public void deleteDislike(Long reviewId, Long userId) {
        String deleteDislikeQuery = "DELETE FROM review_like WHERE review_id = ? AND user_id = ? AND IS_USEFUL = false";
        jdbcTemplate.update(deleteDislikeQuery, reviewId, userId);
        jdbcTemplate.update("UPDATE REVIEWS SET useful = ? WHERE review_id = ?", countUseful(reviewId), reviewId);
    }

    //Полезность
    public Long countUseful(Long reviewId) {
        String countUsefulQuery = "SELECT (SELECT COUNT(review_id) FROM review_like WHERE review_id = ? AND IS_USEFUL = true) - " +
                "(SELECT COUNT(review_id) FROM review_like WHERE review_id = ? AND IS_USEFUL = false) as count_useful";
        SqlRowSet count = jdbcTemplate.queryForRowSet(countUsefulQuery, reviewId, reviewId);
        count.next();
        return count.getLong("count_useful");
    }
}