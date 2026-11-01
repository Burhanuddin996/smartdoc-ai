CREATE TABLE IF NOT EXISTS documents (
    id VARCHAR(64) PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    page_count INT NOT NULL,
    chunk_count INT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW()
);