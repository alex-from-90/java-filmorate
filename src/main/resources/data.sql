MERGE INTO ratings_mpa (id, name, description)
       VALUES (1, 'G', 'У фильма нет возрастных ограничений'),
              (2, 'PG', 'Детям рекомендуется смотреть фильм с родителями'),
              (3, 'PG-13', 'Детям до 13 лет просмотр нежелателен'),
              (4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
              (5, 'NC-17', 'Лицам до 18 лет просмотр запрещён');

MERGE INTO genres (id, name)
       VALUES (1, 'Комедия'),
              (2, 'Драма'),
              (3, 'Мультфильм'),
              (4, 'Триллер'),
              (5, 'Документальный'),
              (6, 'Боевик');