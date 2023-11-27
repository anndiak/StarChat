CREATE TABLE chat_rooms
(
    id SERIAL PRIMARY KEY,
    topic VARCHAR(20),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
)