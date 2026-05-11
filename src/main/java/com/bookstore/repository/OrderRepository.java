package com.bookstore.repository;

import com.bookstore.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbc;

    public OrderRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Long createOrder(String sessionId, BigDecimal totalAmount) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO orders (session_id, total_amount, status) VALUES (?, ?, 'CONFIRMED')",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, sessionId);
            ps.setBigDecimal(2, totalAmount);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void createOrderItem(Long orderId, Long bookId, int quantity, BigDecimal unitPrice) {
        jdbc.update("INSERT INTO order_items (order_id, book_id, quantity, unit_price) VALUES (?, ?, ?, ?)",
                orderId, bookId, quantity, unitPrice);
    }

    public Order findById(Long orderId) {
        return jdbc.queryForObject("SELECT * FROM orders WHERE id = ?",
            (rs, rn) -> {
                Order o = new Order();
                o.setId(rs.getLong("id"));
                o.setSessionId(rs.getString("session_id"));
                o.setTotalAmount(rs.getBigDecimal("total_amount"));
                o.setStatus(rs.getString("status"));
                o.setPlacedAt(rs.getTimestamp("placed_at").toLocalDateTime());
                return o;
            }, orderId);
    }
}
