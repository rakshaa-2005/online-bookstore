package com.bookstore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartItem {

    private Long id;
    private String sessionId;
    private Long userId;
    private Long bookId;
    private int quantity;
    private LocalDateTime addedAt;

    // Joined from books table for convenience
    private String bookTitle;
    private String bookAuthor;
    private BigDecimal bookPrice;
    private String bookCoverUrl;

    // ── Constructors ──────────────────────────────────────────────────────────

    public CartItem() {}

    public CartItem(Long id, String sessionId, Long userId, Long bookId, int quantity, LocalDateTime addedAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.addedAt = addedAt;
    }

    // ── Computed ──────────────────────────────────────────────────────────────

    public BigDecimal getSubtotal() {
        if (bookPrice == null) return BigDecimal.ZERO;
        return bookPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }

    public BigDecimal getBookPrice() { return bookPrice; }
    public void setBookPrice(BigDecimal bookPrice) { this.bookPrice = bookPrice; }

    public String getBookCoverUrl() { return bookCoverUrl; }
    public void setBookCoverUrl(String bookCoverUrl) { this.bookCoverUrl = bookCoverUrl; }
}
