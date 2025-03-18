package com.praca.thymeleaf.backend.services;

import java.util.List;
import com.praca.thymeleaf.backend.models.Order;
import com.praca.thymeleaf.backend.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        // Ustaw datę i status zamówienia
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        // Oblicz całkowitą cenę zamówienia
        double totalPrice = order.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice())
                .sum();
        order.setTotalPrice(totalPrice);

        // Zapisz zamówienie w bazie danych
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public void updateOrderStatus(Long orderId, String status) {
        // Pobierz zamówienie na podstawie ID
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Zaktualizuj status zamówienia
        order.setStatus(status);

        // Zapisz zmiany w bazie danych
        orderRepository.save(order);
    }


}
