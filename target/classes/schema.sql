CREATE TABLE IF NOT EXISTS books (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255)   NOT NULL,
    author      VARCHAR(255)   NOT NULL,
    description CLOB,
    price       DECIMAL(10, 2) NOT NULL,
    cover_url   VARCHAR(500),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    book_id    BIGINT NOT NULL REFERENCES books (id) ON DELETE CASCADE,
    quantity   INT NOT NULL DEFAULT 1,
    added_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id   VARCHAR(255),
    total_amount DECIMAL(10, 2) NOT NULL,
    status       VARCHAR(50) DEFAULT 'CONFIRMED',
    placed_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    book_id    BIGINT NOT NULL REFERENCES books (id),
    quantity   INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL
);
