DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS film_likes CASCADE;
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS review_like CASCADE;
DROP TABLE IF EXISTS feed CASCADE;

CREATE TABLE IF NOT EXISTS ratings_mpa
(
    id          int generated by default as identity primary key,
    name        varchar(255) NOT NULL UNIQUE,
    description varchar(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS films
(
    id           bigint generated by default as identity,
    name         varchar(255) NOT NULL,
    description  varchar(200),
    release_date date         NOT NULL,
    duration     int          NOT NULL,
    rating_id    int          NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_rating_id FOREIGN KEY (rating_id) REFERENCES ratings_mpa (id) ON DELETE RESTRICT
    );

CREATE TABLE IF NOT EXISTS genres
(
    id   int generated by default as identity primary key,
    name varchar(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  bigint REFERENCES films (id) ON DELETE CASCADE,
    genre_id int REFERENCES genres (id) ON DELETE RESTRICT,
    PRIMARY KEY (film_id, genre_id)
    );

CREATE TABLE IF NOT EXISTS users
(
    id       bigint generated by default as identity primary key UNIQUE,
    email    varchar(255) NOT NULL,
    login    varchar(255) NOT NULL UNIQUE,
    name     varchar(255),
    birthday date
    );

CREATE TABLE IF NOT EXISTS film_likes
(
    film_id  bigint REFERENCES films (id) ON DELETE CASCADE,
    user_id bigint REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
    );

CREATE TABLE IF NOT EXISTS friends
(
    user_id bigint REFERENCES users (id) ON DELETE CASCADE,
    friend_id bigint REFERENCES users (id) ON DELETE CASCADE,
    status boolean,
    PRIMARY KEY (user_id, friend_id)
    );
CREATE TABLE IF NOT EXISTS reviews
(
    review_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR(200) NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    film_id BIGINT REFERENCES films (id) ON DELETE CASCADE,
    useful INT
);

CREATE TABLE IF NOT EXISTS review_like
(
    review_id BIGINT REFERENCES reviews (review_id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    is_useful BOOLEAN,
    primary key (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS feed
(
    event_time TIMESTAMP NOT NULL,
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    event_type VARCHAR(10) NOT NULL,
    operation VARCHAR(10) NOT NULL,
    event_id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    CONSTRAINT constr_type CHECK (event_type IN ('LIKE','REVIEW','FRIEND')),
    CONSTRAINT constr_operation CHECK (operation IN ('REMOVE','ADD','UPDATE'))
)