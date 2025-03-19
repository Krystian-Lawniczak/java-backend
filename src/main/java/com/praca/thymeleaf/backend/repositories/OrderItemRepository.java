package com.praca.thymeleaf.backend.repositories;

import com.praca.thymeleaf.backend.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 🔹 Pobiera wszystkie pozycje dla danego zamówienia
    List<OrderItem> findByOrderId(Long orderId);

    // 🔹 Usuwa wszystkie pozycje zamówienia
    void deleteByOrderId(Long orderId);
}
