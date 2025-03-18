package com.praca.thymeleaf.backend.controllers;

import com.praca.thymeleaf.backend.models.Order;
import com.praca.thymeleaf.backend.services.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }

    @PutMapping("/{orderId}/status")
    public void updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
    }
}
