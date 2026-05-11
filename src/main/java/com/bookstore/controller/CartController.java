package com.bookstore.controller;

import com.bookstore.model.CartItem;
import com.bookstore.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for cart operations.
 *
 * GET    /cart          → get all cart items for current session
 * POST   /cart          → add a book to cart  { "bookId": 1, "quantity": 1 }
 * DELETE /cart/{itemId} → remove a cart item
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET /cart
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(HttpSession session) {
        String sid = session.getId();
        List<CartItem> items = cartService.getCart(sid);
        BigDecimal total = cartService.getCartTotal(sid);
        return ResponseEntity.ok(Map.of(
                "items", items,
                "total", total,
                "count", items.stream().mapToInt(CartItem::getQuantity).sum()
        ));
    }

    // POST /cart
    // Body: { "bookId": 1, "quantity": 1 }
    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Object> body,
                                       HttpSession session) {
        try {
            Long bookId = Long.parseLong(body.get("bookId").toString());
            int quantity = body.containsKey("quantity")
                    ? Integer.parseInt(body.get("quantity").toString()) : 1;

            CartItem item = cartService.addToCart(session.getId(), bookId, quantity);
            return ResponseEntity.ok(item);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /cart/{itemId}
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long itemId, HttpSession session) {
        boolean removed = cartService.removeFromCart(itemId, session.getId());
        if (removed) {
            return ResponseEntity.ok(Map.of("message", "Item removed from cart"));
        }
        return ResponseEntity.notFound().build();
    }
}
