package com.praca.thymeleaf.backend.controllers;

import com.praca.thymeleaf.backend.models.CartRequest;
import com.praca.thymeleaf.backend.models.Order;
import com.praca.thymeleaf.backend.models.User;
import com.praca.thymeleaf.backend.repositories.UserRepository;
import com.praca.thymeleaf.backend.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository; // 🔹 Dodano UserRepository

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository; // 🔹 Wstrzyknięcie zależności
    }

    // 🔹 Pobieranie zawartości koszyka użytkownika
    @GetMapping("/cart/{userId}")
    public ResponseEntity<Order> getCart(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(orderService.getOrCreateCart(user));
    }

    // 🔹 Dodawanie produktu do koszyka
    @PostMapping("/cart/add")
    public ResponseEntity<Order> addToCart(@RequestBody CartRequest request) {
        return ResponseEntity.ok(orderService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity()));
    }


    // 🔹 Usuwanie produktu z koszyka
    @DeleteMapping("/cart/remove")
    public ResponseEntity<Order> removeFromCart(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        return ResponseEntity.ok(orderService.removeFromCart(userId, productId));
    }

    // 🔹 Finalizacja zamówienia – konwersja koszyka w zamówienie
    @PostMapping("/cart/finalize/{userId}")
    public ResponseEntity<Order> finalizeOrder(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.finalizeOrder(userId));
    }

    // 🔹 Pobieranie historii zamówień użytkownika
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    // 🔹 Aktualizacja statusu zamówienia
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok().build();
    }
}
