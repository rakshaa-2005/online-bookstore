package com.bookstore.service;

import com.bookstore.model.CartItem;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepo;
    private final BookRepository bookRepo;

    public CartService(CartRepository cartRepo, BookRepository bookRepo) {
        this.cartRepo = cartRepo;
        this.bookRepo = bookRepo;
    }

    /** Return all cart items for the given session. */
    public List<CartItem> getCart(String sessionId) {
        return cartRepo.findBySession(sessionId);
    }

    /** Total price of the cart. */
    public BigDecimal getCartTotal(String sessionId) {
        return getCart(sessionId).stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Add a book to the cart.
     * If the book is already in the cart, increment its quantity.
     */
    public CartItem addToCart(String sessionId, Long bookId, int quantity) {
        // Validate book exists
        bookRepo.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        Optional<CartItem> existing = cartRepo.findBySessionAndBook(sessionId, bookId);
        if (existing.isPresent()) {
            int newQty = existing.get().getQuantity() + quantity;
            cartRepo.updateQuantity(existing.get().getId(), newQty);
        } else {
            cartRepo.addItem(sessionId, bookId, quantity);
        }

        // Return updated item
        return cartRepo.findBySessionAndBook(sessionId, bookId)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve cart item after add"));
    }

    /** Remove an item from the cart. Returns false if item not found / belongs to another session. */
    public boolean removeFromCart(Long cartItemId, String sessionId) {
        int rows = cartRepo.removeItem(cartItemId, sessionId);
        return rows > 0;
    }

    /** Clear all items from the cart (called after checkout). */
    public void clearCart(String sessionId) {
        cartRepo.clearCart(sessionId);
    }
}
