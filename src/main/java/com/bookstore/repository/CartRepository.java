package com.bookstore.repository;

import com.bookstore.model.CartItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CartRepository {

    private final JdbcTemplate jdbc;

    public CartRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<CartItem> cartMapper = (rs, rowNum) -> {
        CartItem c = new CartItem();
        c.setId(rs.getLong("id"));
        c.setSessionId(rs.getString("session_id"));
        c.setBookId(rs.getLong("book_id"));
        c.setQuantity(rs.getInt("quantity"));
        c.setBookTitle(rs.getString("title"));
        c.setBookAuthor(rs.getString("author"));
        c.setBookPrice(rs.getBigDecimal("price"));
        c.setBookCoverUrl(rs.getString("cover_url"));
        return c;
    };

    public List<CartItem> findBySession(String sessionId) {
        return jdbc.query(
            "SELECT ci.*, b.title, b.author, b.price, b.cover_url FROM cart_items ci " +
            "JOIN books b ON ci.book_id = b.id WHERE ci.session_id = ? ORDER BY ci.added_at",
            cartMapper, sessionId);
    }

    public Optional<CartItem> findBySessionAndBook(String sessionId, Long bookId) {
        List<CartItem> result = jdbc.query(
            "SELECT ci.*, b.title, b.author, b.price, b.cover_url FROM cart_items ci " +
            "JOIN books b ON ci.book_id = b.id WHERE ci.session_id = ? AND ci.book_id = ?",
            cartMapper, sessionId, bookId);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public void addItem(String sessionId, Long bookId, int quantity) {
        jdbc.update("INSERT INTO cart_items (session_id, book_id, quantity) VALUES (?, ?, ?)",
                sessionId, bookId, quantity);
    }

    public void updateQuantity(Long cartItemId, int newQuantity) {
        jdbc.update("UPDATE cart_items SET quantity = ? WHERE id = ?", newQuantity, cartItemId);
    }

    public int removeItem(Long cartItemId, String sessionId) {
        return jdbc.update("DELETE FROM cart_items WHERE id = ? AND session_id = ?", cartItemId, sessionId);
    }

    public void clearCart(String sessionId) {
        jdbc.update("DELETE FROM cart_items WHERE session_id = ?", sessionId);
    }
}
