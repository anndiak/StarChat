CREATE TABLE user_presences
(
    email VARCHAR(50) PRIMARY KEY ,
    is_online SMALLINT NOT NULL,
    last_login TIMESTAMP NOT NULL
)