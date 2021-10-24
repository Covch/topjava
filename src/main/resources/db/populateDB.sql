DELETE FROM meals;
DELETE FROM user_roles;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, date_time, description, calories)
VALUES (100000, '2020-12-19 10:07:04', 'Breakfast user', 416);
INSERT INTO meals (user_id, date_time, description, calories)
VALUES (100001, '2020-11-12 05:31:33', 'Afternoon snack admin', 1084);
INSERT INTO meals (user_id, date_time, description, calories)
VALUES (100000, '2021-12-19 10:07:04', 'Second breakfast user', 1231);