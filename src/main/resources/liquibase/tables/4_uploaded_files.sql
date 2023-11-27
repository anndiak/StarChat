CREATE TABLE uploaded_files
(
    id SERIAL PRIMARY KEY,
    message_id INT REFERENCES messages(id),
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_size INT NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL
)