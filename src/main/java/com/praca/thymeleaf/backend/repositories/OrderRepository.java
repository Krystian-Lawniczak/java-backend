package com.praca.thymeleaf.backend.repositories;

import com.praca.thymeleaf.backend.models.Order;
import com.praca.thymeleaf.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 🔹 Pobieranie wszystkich zamówień użytkownika
    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdAndIsCart(Long userId, boolean isCart);

    // 🔹 Pobieranie aktywnego koszyka użytkownika (jeśli istnieje)
    Optional<Order> findByUserAndIsCart(User user, boolean isCart);
}
