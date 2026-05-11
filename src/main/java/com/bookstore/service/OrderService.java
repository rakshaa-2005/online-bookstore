package com.bookstore.service;

import com.bookstore.model.CartItem;
import com.bookstore.model.Order;
import com.bookstore.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepo, CartService cartService) {
        this.orderRepo = orderRepo;
        this.cartService = cartService;
    }

    @Transactional
    public Order checkout(String sessionId) {
        List<CartItem> items = cartService.getCart(sessionId);
        if (items.isEmpty()) throw new IllegalStateException("Cart is empty");

        BigDecimal total = cartService.getCartTotal(sessionId);
        Long orderId = orderRepo.createOrder(sessionId, total);

        for (CartItem item : items) {
            orderRepo.createOrderItem(orderId, item.getBookId(), item.getQuantity(), item.getBookPrice());
        }

        cartService.clearCart(sessionId);
        return orderRepo.findById(orderId);
    }
}