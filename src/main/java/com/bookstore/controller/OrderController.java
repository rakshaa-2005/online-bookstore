package com.bookstore.controller;

import com.bookstore.model.Order;
import com.bookstore.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(HttpSession session) {
        try {
            Order order = orderService.checkout(session.getId());
            return ResponseEntity.ok(Map.of(
                "message", "Order placed successfully!",
                "orderId", order.getId(),
                "total", order.getTotalAmount(),
                "status", order.getStatus(),
                "placedAt", order.getPlacedAt().toString()
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
