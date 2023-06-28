package ru.yandex.practicum.filmorate.storage.database.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * //По отзывам в ревью используем BeanPropertyRowMapper вместо маппера
 * Упростили проверку на наличие записи в таблице
 * <p>
 * Используемые Spring-методы:
 * - `BeanPropertySqlParameterSource` используется для создания параметров запроса на основе свойств объекта отзыва.
 * Это позволяет автоматически сопоставить значения свойств объекта с именами параметров в SQL-запросе.
 * Данный метод использует рефлексию для получения значения свойств объекта.
 * `
 * -  SimpleJdbcInsert` используется для упрощения вставки данных в таблицу базы данных.
 * Он позволяет указать имя таблицы и генерируемые столбцы сгенерированного ключа (если применимо).
 * В данном случае, объект `SimpleJdbcInsert` настроен на работу с таблицей "REVIEWS" и указание столбца "review_id"
 * как генерируемого ключа. Затем выполняется вставка данных в таблицу и возвращается сгенерированный идентификатор отзыва.
 * <p>
 * - `NamedParameterJdbcTemplate` предоставляет удобный способ выполнения именованных запросов с параметрами.
 * В данном случае, он используется для выполнения запроса на получение списка отзывов.
 * <p>
 * - `MapSqlParameterSource` используется для создания параметров запроса на основе карты значений.
 * Каждая пара ключ-значение из карты представляет собой имя параметра и его значение.
 * В данном случае, создается объект `MapSqlParameterSource` на основе карты значений `paramMap`,
 * которая содержит параметры запроса filmId и count.
 * <p>
 * - `BeanPropertyRowMapper` используется для маппинга результатов запроса в объекты отзывов.
 * Этот маппер автоматически сопоставляет столбцы результирующего набора данных с полями объекта отзыва
 * на основе их имен. Возвращается список отзывов, полученных из результирующего набора.
 **/

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
            throw new NotFoundException(
                    String.format("Неверный id (%d) при добавлении", review.getFilmId()));
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName(
                        "REVIEWS")
                .usingGeneratedKeyColumns("review_id");
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(review);
        review.setReviewId(simpleJdbcInsert.executeAndReturnKey(parameterSource)
                .longValue());

        return review;
    }

    public List<Review> getAll(Long filmId, Long count) {
        String getAllReviewsQuery
                = "SELECT * FROM reviews WHERE (:filmId IS NULL OR film_id = :filmId) ORDER BY useful DESC LIMIT :count";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("filmId", filmId);
        paramMap.put("count", count);

        SqlParameterSource paramSource = new MapSqlParameterSource(paramMap);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
                jdbcTemplate);
        //По отзывам в ревью используем BeanPropertyRowMapper вместо маппера
        return namedParameterJdbcTemplate.query(getAllReviewsQuery, paramSource,
                new BeanPropertyRowMapper<>(Review.class));
    }

    public Optional<Review> getById(Long reviewId) {
        String getByIdQuery = "SELECT * FROM reviews WHERE review_id = ?";
        Object[] args = new Object[]{reviewId};
        int[] argTypes = new int[]{Types.BIGINT};

        try { //По отзывам в ревью используем BeanPropertyRowMapper вместо маппера
            Review review = jdbcTemplate.queryForObject(getByIdQuery, args, argTypes,
                    BeanPropertyRowMapper.newInstance(Review.class));
            return Optional.ofNullable(review);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Review not found with id = %d", reviewId));
        }
    }

    public Review update(Review review) {
        Optional<Review> existingReview = getById(review.getReviewId());
        if (existingReview.isPresent()) {
            Review oldReview = existingReview.get();
            review.setUserId(oldReview.getUserId());
            review.setFilmId(oldReview.getFilmId());
            review.setUseful(oldReview.getUseful());

            String updateReviewByIdQuery
                    = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
            jdbcTemplate.update(updateReviewByIdQuery, review.getContent(), review.getIsPositive(),
                    review.getReviewId());

            return review;
        } else {
            throw new NotFoundException(String.format("Неверный id отзыва при обновлении. id = %d",
                    review.getReviewId()));
        }
    }

    public void deleteById(Long reviewId) {
        String deleteReviewByIdQuery = "DELETE FROM REVIEWS WHERE review_id = ?";
        int updateCount = jdbcTemplate.update(deleteReviewByIdQuery, reviewId);

        if (updateCount == 0) {
            throw new NotFoundException(
                    String.format("Неверный id при удалении: id = %d", reviewId));
        }
    }

    //Лайки
    //Добавить лайк
    public void addLike(Long reviewId, Long userId) {
        deleteDislike(reviewId, userId); //При лайке удаляем дизлайк
        String addLikeQuery
                = "INSERT INTO review_like (review_id, user_id, IS_USEFUL) VALUES (?,?,true)";
        jdbcTemplate.update(addLikeQuery, reviewId, userId);
        jdbcTemplate.update("UPDATE REVIEWS SET useful = ? WHERE review_id = ?",
                countUseful(reviewId), reviewId);
    }

    //Удалить лайк
    public void deleteLike(Long reviewId, Long userId) {
        String deleteLikeQuery
                = "DELETE FROM review_like WHERE review_id = ? AND user_id = ? AND IS_USEFUL = true";
        jdbcTemplate.update(deleteLikeQuery, reviewId, userId);
        jdbcTemplate.update("UPDATE REVIEWS SET useful = ? WHERE review_id = ?",
                countUseful(reviewId), reviewId);
    }

    //Добавить дизлайк
    public void addDislike(Long reviewId, Long userId) {
        deleteLike(reviewId, userId); // При длизлайке удаляем лайк
        String addDislikeQuery
                = "INSERT INTO review_like (review_id, user_id, IS_USEFUL) VALUES (?,?,false)";
        jdbcTemplate.update(addDislikeQuery, reviewId, userId);
        jdbcTemplate.update("UPDATE REVIEWS SET useful = ? WHERE review_id = ?",
                countUseful(reviewId), reviewId);
    }

    //Удалить дизлайк
    public void deleteDislike(Long reviewId, Long userId) {
        String deleteDislikeQuery
                = "DELETE FROM review_like WHERE review_id = ? AND user_id = ? AND IS_USEFUL = false";
        jdbcTemplate.update(deleteDislikeQuery, reviewId, userId);
        jdbcTemplate.update("UPDATE REVIEWS SET useful = ? WHERE review_id = ?",
                countUseful(reviewId), reviewId);
    }

    //Полезность
    public Long countUseful(Long reviewId) {
        //@formatter:off
        String countUsefulQuery = "SELECT COUNT(CASE WHEN IS_USEFUL = true THEN 1 END) - "
                + "COUNT(CASE WHEN IS_USEFUL = false THEN 1 END) AS count_useful "
                + "FROM review_like WHERE review_id = ?";
        //@formatter:on
        return jdbcTemplate.queryForObject(countUsefulQuery, Long.class, reviewId);
    }
}