package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.*;

@Primary
@Component
@RequiredArgsConstructor
public class ReviewDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    public static final String GET_REVIEW_BY_ID_QUERY_TEMPLATE = "SELECT * FROM REVIEWS WHERE review_id = ?";
    public static final String UPDATE_USEFUL_IN_REVIEWS_QUERY_TEMPLATE = "UPDATE REVIEWS SET useful = ? " +
            "WHERE review_id = ?";

    public Review add(Review review) {
        review.setUseful(0L);

        User user = userDbStorage.getUserById(review.getUserId());
        if (user == null) {
            throw new NotFoundException(String.format("Attempt to create review by user with absent id = %d", review.getUserId()));
        }

        Film film = filmDbStorage.getFilmById(review.getFilmId());
        if (film == null) {
            throw new NotFoundException(String.format("Attempt to create review to film with absent id = %d", review.getFilmId()));
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
            getAllReviewsQuery = "SELECT * FROM REVIEWS ORDER BY useful DESC LIMIT ?";
            args = new Object[]{count};
            argTypes = new int[]{Types.BIGINT};
        } else {
            getAllReviewsQuery = "SELECT * FROM REVIEWS WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            args = new Object[]{filmId, count};
            argTypes = new int[]{Types.BIGINT, Types.BIGINT};
        }
        return jdbcTemplate.query(getAllReviewsQuery, args, argTypes, (rs, rowNum) -> {
            Review review = new Review();
            review.setReviewId(rs.getLong("review_id"));
            review.setContent(rs.getString("content"));
            review.setIsPositive(rs.getBoolean("is_positive"));
            review.setUserId(rs.getLong("user_id"));
            review.setFilmId(rs.getLong("film_id"));
            review.setUseful(rs.getLong("useful"));
            return review;
        });
    }

    public Optional<Review> getById(Long reviewId) {
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet(GET_REVIEW_BY_ID_QUERY_TEMPLATE, reviewId);
        if (reviewRows.first()) {
            Review review = new Review();
            review.setReviewId(reviewRows.getLong("review_id"));
            review.setContent(reviewRows.getString("content"));
            review.setIsPositive(reviewRows.getBoolean("is_positive"));
            review.setUserId(reviewRows.getLong("user_id"));
            review.setFilmId(reviewRows.getLong("film_id"));
            review.setUseful(reviewRows.getLong("useful"));

            return Optional.of(review);
        } else {
            return Optional.empty();
        }
    }
    public Review update(Review review) {
        Optional<Review> existingReview = getById(review.getReviewId());
        if (existingReview.isPresent()) {
            String updateReviewByIdQuery = "UPDATE REVIEWS SET content = ?, is_positive = ? WHERE review_id = ?";
            jdbcTemplate.update(updateReviewByIdQuery,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getReviewId());

            return review;
        } else {
            throw new NotFoundException(String.format("Attempt to update review with absent id = %d", review.getReviewId()));
        }
    }
}
