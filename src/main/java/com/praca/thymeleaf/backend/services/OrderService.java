package com.praca.thymeleaf.backend.services;

import com.praca.thymeleaf.backend.models.Order;
import com.praca.thymeleaf.backend.models.OrderItem;
import com.praca.thymeleaf.backend.models.Product;
import com.praca.thymeleaf.backend.models.User;
import com.praca.thymeleaf.backend.repositories.OrderRepository;
import com.praca.thymeleaf.backend.repositories.OrderItemRepository;
import com.praca.thymeleaf.backend.repositories.ProductRepository;
import com.praca.thymeleaf.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        UserRepository userRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // 🔹 Pobieramy aktywny koszyk użytkownika (jeśli nie istnieje, tworzymy nowy)
    public Order getOrCreateCart(User user) { // Teraz przyjmuje obiekt User zamiast userId
        return orderRepository.findByUserAndIsCart(user, true)
                .orElseGet(() -> {
                    Order newCart = new Order();
                    newCart.setUser(user);
                    newCart.setIsCart(true);  // ✅ Poprawna wersja
                    newCart.setOrderDate(LocalDateTime.now());
                    newCart.setStatus("CART");
                    return orderRepository.save(newCart);
                });
    }



    // 🔹 Dodawanie produktu do koszyka
    public Order addToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Order cart = getOrCreateCart(user);

        // Sprawdzamy, czy produkt już jest w koszyku
        Optional<OrderItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            OrderItem newItem = new OrderItem(product, quantity);
            newItem.setOrder(cart);
            cart.getItems().add(newItem);
        }

        cart.calculateTotalPrice();
        return orderRepository.save(cart);
    }

    // 🔹 Usuwanie produktu z koszyka
    public Order removeFromCart(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Order cart = getOrCreateCart(user);

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));

        cart.calculateTotalPrice();
        return orderRepository.save(cart);
    }

    // 🔹 Finalizacja zamówienia – zamiana koszyka w zamówienie
    public Order finalizeOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Order cart = getOrCreateCart(user);

        if (cart.getItems().isEmpty()) {
            System.out.println("❌ Koszyk jest pusty – nie można złożyć zamówienia.");
            return cart; // lub ResponseEntity.badRequest() po stronie kontrolera
        }


        cart.setIsCart(false);
        cart.setOrderDate(LocalDateTime.now());
        cart.setStatus("PENDING");

        return orderRepository.save(cart);
    }

    // 🔹 Pobieranie zamówień użytkownika
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdAndIsCart(userId, false); // tylko finalne zamówienia
    }

    // 🔹 Aktualizacja statusu zamówienia
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        orderRepository.save(order);
    }
}
