package com.praca.thymeleaf.backend.repositories;

import com.praca.thymeleaf.backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Dodatkowa metoda: Pobieranie zamówień użytkownika po jego ID
    List<Order> findByUserId(Long userId);
}
