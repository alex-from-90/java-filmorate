<br/>

<h3 align="center">Filmorate</h3>
  <p align="center">
    yandex, filmorate
    <br/>
    <br/>
    <a href="https://github.com/alex-from-90/java-filmorate"><strong>Проект »</strong></a>
    <br/>
    <br/>
  </p>

## О проекте

Этот проект - это платформа для любителей фильмов, которая предоставляет возможность пользователям выбирать,  и оценивать свои любимые фильмы, а также находить наиболее популярные среди них. Так же есть возможность добавлять друзей

## Основан на: 

Java Amazon correctto 11 (11.0.19)
Spring Boot 2.7
Dependencies:
Spring Web WEB
Build web, including RESTful, applications using Spring MVC. Uses Apache Tomcat as the default embedded container.

## Описание базы данных
<details><summary>Структура базы данных</summary>
<a href = "diagram.png" target ="_blank"><img src="diagram.png" alt="Logo" /></a>

**Таблица "film":**

- "film_id" - уникальный идентификатор фильма
- "title" - название фильма
- "description" - описание фильма
- "duration" - длительность фильма в минутах
- "release_date" - дата выпуска фильма

**Таблица "film_genre":**

- "film_id" - идентификатор фильма
- "genre_id" - идентификатор жанра
- "mpa_rating_id" - идентификатор рейтинга MPAA

**Таблица "film_like":**

- "film_id" - идентификатор фильма
- "user_id" - идентификатор пользователя

 **Таблица "friends":**

- "user_id" - идентификатор пользователя
- "friend_id" - идентификатор друга
- "status" - статус отношений между пользователями (логическое значение true/false)


**Таблица "genres":**

- "genre_id" - уникальный идентификатор жанра
- "genre_name" - название жанра 
 
**Таблица "mpa_rating":**

- "mpa_rating_id" - уникальный идентификатор рейтинга MPAA
- "mpa_name" - название рейтинга MPAA

**Таблица "user":**

- "user_id" - уникальный идентификатор пользователя
- "email" - адрес электронной почты пользователя
- "login" - логин пользователя
- "name" - имя пользователя
- "birthday" - дата рождения пользователя

**Внешние ключи:**

- "film_genre_film_id_foreign" - внешний ключ для связи с таблицей "film" по полю "film_id"
- "film_genre_genre_id_foreign" - внешний ключ для связи с таблицей "genres" по полю "genre_id"
- "film_mpa_rating_id_foreign" - внешний ключ для связи с таблицей "mpa_rating" по полю "mpa_rating_id"
- "film_like_film_id_foreign" - внешний ключ для связи с таблицей "film" по полю "film_id"
- "film_like_user_id_foreign" - внешний ключ для связи с таблицей "user" по полю "user_id"
- "friends_to_user_id_foreign" - внешний ключ для связи с таблицей "user" по полю "user_id"

**Ограничения и ключи:**
- "film_pkey" - первичный ключ для таблицы "film" по полю "film_id"
- "film_genre_pkey" - первичный ключ для таблицы "film_genre" по полям "film_id", "genre_id" и "mpa_rating_id"
- "film_like_pkey" - первичный ключ для таблицы "film_like" по полям "film_id" и "user_id"
- "friends_pkey" - первичный ключ для таблицы "friends" по полям "user_id" и "friend_id"
- "friends_to_user_id_unique" - уникальное значение поля "user_id" в таблице "friends"
- "genres_pkey" - первичный ключ для таблицы "genres" по полю "genre_id"
- "genres_name_unique" - уникальное значение поля "genre_name" в таблице "genres"
- "mpa_rating_pkey" - первичный ключ для таблицы "mpa_rating" по полю "mpa_rating_id"
- "mpa_rating_name_unique" - уникальное значение поля "mpa_name" в таблице "mpa_rating"
- "user_pkey" - первичный ключ для таблицы "user" по полю "user_id"
- "user_email_unique" - уникальное значение поля "email" в таблице "user"
- "user_login_unique" - уникальное значение поля "login" в таблице "user"
- "film_genre_film_id_foreign" - внешний ключ для связи с таблицей "film" по полю "film_id" в таблице "film_genre"
- "film_genre_genre_id_foreign" - внешний ключ для связи с таблицей "genres" по полю "genre_id" в таблице "film_genre"
- "film_mpa_rating_id_foreign" - внешний ключ для связи с таблицей "mpa_rating" по полю "mpa_rating_id" в таблице "film_genre"
- "film_like_film_id_foreign" - внешний ключ для связи с таблицей "film" по полю "film_id" в таблице "film_like"
- "film_like_user_id_foreign" - внешний ключ для связи с таблицей "user" по полю "user_id" в таблице "film_like"
- "friends_to_user_id_foreign" - внешний ключ для связи с таблицей "user" по полю "user_id" в таблице "friends"


</details>

## Использование

Примеры запросов к базе данных:


 <details><summary>Добавить фильм</summary>
    <pre>
   INSERT INTO film (film_id, title, description, duration, release_date)
VALUES (ID, 'TITLE', 'DESCRIPTION', DURATION , 'RELEASE (YYYY-MM-DD)';
</pre>

**Так же  нужно добавить жанр и рейтинг MPA**

<pre>
INSERT INTO film_genre (film_id, genre_id, mpa_rating_id)
VALUES (FILM_ID, GENRE_ID, MPA_RATING_ID);
    </pre>

Список жанров с id:
- **1** Комедия.
- **2** Драма.
- **3** Мультфильм.
- **4** Триллер.
- **5** Документальный.
- **6** Боевик.

Список рейтингов с id:
- **1** G — у фильма нет возрастных ограничений,
- **2** PG — детям рекомендуется смотреть фильм с родителями,
- **3** PG-13 — детям до 13 лет просмотр не желателен,
- **4** R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
- **5** NC-17 — лицам до 18 лет просмотр запрещён.
   </details>


   <details><summary>Добавить Like фильму</summary>
    <pre>
    lINSERT INTO film_like (film_id, user_id)  VALUES (FILM_ID, 1);
    </pre>
   </details>

   <details><summary>Добавить в друзья</summary>
    <pre>
INSERT INTO friends (user_id, friend_id, status)  VALUES (USER_ID, FRIEND_ID, 'FALSE')
    </pre>

**Подтверждение дружбы**

<pre>
UPDATE friends
SET status = TRUE
WHERE user_id = ID_ПОЛЬЗОВАТЕЛЯ;
    </pre>

   </details>


   <details><summary>Добавление пользователя</summary>
    <pre>
   INSERT INTO "user" (user_id, email, login, name, birthdate)
VALUES (ID, 'USER_EMAIL', 'USER_LOGIN', 'USER_NAME', 'DATA OF BIRTHDAY YYYY-MM-DD');
    </pre>

**Пример обновления данных пользователя**

<pre>
UPDATE "user"
SET email = 'NEW_EMAIL'
WHERE user_id = ID;
</pre>
   </details>

<details><summary>Добавление жанров фильмов в таблицу с жанрами</summary>
    <pre>
INSERT INTO genres (genre_id, genre_name) VALUES (4, 'Триллер');
INSERT INTO genres (genre_id, genre_name) VALUES (5, 'Документальный');
INSERT INTO genres (genre_id, genre_name) VALUES (6, 'Боевик');
  </pre>
 </details>

<details><summary>Добавление рейтингов фильмов в таблицу с рейтингами</summary>
    <pre>
INSERT INTO mpa_rating (mpa_rating_id, mpa_name) VALUES (2, 'PG');
  </pre>
 </details>

<details><summary>Получение MPA рейтинга фильма по id фильма</summary>
    <pre>
SELECT mpa.mpa_name
FROM film_genre fg
JOIN mpa_rating mpa ON fg.mpa_rating_id = mpa.mpa_rating_id
WHERE fg.film_id = FILM_ID;
  </pre>
 </details>

<details><summary>Получение жанра фильма по id фильма</summary>
    <pre>
SELECT g.genre_name
FROM film_genre fg
JOIN genres g ON fg.genre_id = g.genre_id
WHERE fg.film_id = FILM_ID;
  </pre>
 </details>

<details><summary>Показать друзей пользователя c ID 1</summary>
    <pre>
SELECT u.user_id, u.name, u.email
FROM friends f
JOIN "user" u ON f.friend_id = u.user_id
WHERE f.user_id = 1 AND f.status = true;
  </pre>
 </details>

<details><summary>Получить 10 самых популярных фильмов (рейтинг основан на количестве лайков </summary>
    <pre>
SELECT f.film_id, f.title, COUNT(DISTINCT fl.user_id) AS like_count
FROM film f
LEFT JOIN film_like fl ON f.film_id = fl.film_id
GROUP BY f.film_id, f.title
ORDER BY like_count DESC
LIMIT 10;
  </pre>
 </details>









