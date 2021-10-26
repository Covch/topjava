DELETE
FROM meals;
DELETE
FROM user_roles;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, date_time, description, calories)
VALUES (100000, '2020-12-21 10:00', 'User`s meal 1', 100),
       (100000, '2021-12-25 11:00', 'User`s meal 2', 200),
       (100000, '2021-12-15 00:00', 'User`s meal 3', 300),
       (100000, '2021-12-15 00:01', 'User`s meal 4', 400),
       (100000, '2021-12-14 23:59', 'User`s meal 5', 500),
       (100001, '2020-11-12 05:31', 'Admin`s meal 1 for deleting', 100),
       (100001, '1000-01-01 01:01', 'Admin`s meal 1 for updating', 100);