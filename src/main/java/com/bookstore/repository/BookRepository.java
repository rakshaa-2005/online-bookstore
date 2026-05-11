package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {

    private final JdbcTemplate jdbc;

    public BookRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Book> bookMapper = (rs, rowNum) -> {
        Book b = new Book();
        b.setId(rs.getLong("id"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setDescription(rs.getString("description"));
        b.setPrice(rs.getBigDecimal("price"));
        b.setCoverUrl(rs.getString("cover_url"));
        return b;
    };

    public List<Book> findAll() {
        return jdbc.query("SELECT * FROM books ORDER BY id", bookMapper);
    }

    public List<Book> search(String query) {
        String like = "%" + query.toLowerCase() + "%";
        return jdbc.query(
            "SELECT * FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? ORDER BY id",
            bookMapper, like, like);
    }

    public Optional<Book> findById(Long id) {
        List<Book> result = jdbc.query("SELECT * FROM books WHERE id = ?", bookMapper, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public void decreaseStock(Long bookId, int quantity) {
        // no stock column in simplified version — no-op
    }
}
