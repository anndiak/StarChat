CREATE TABLE users
(
    id         SERIAL  PRIMARY KEY,
    email      VARCHAR(40) UNIQUE,
    phone      VARCHAR(25) UNIQUE,
    first_name VARCHAR (30),
    last_name VARCHAR(30),
    password   VARCHAR(200),
    user_role int,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)