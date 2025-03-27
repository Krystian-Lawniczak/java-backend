package com.praca.thymeleaf.backend.repositories;

import com.praca.thymeleaf.backend.models.Favorite;
import com.praca.thymeleaf.backend.models.User;
import com.praca.thymeleaf.backend.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndProduct(User user, Product product);

    List<Favorite> findByUserId(Long userId);

    Optional<Favorite> findByUserAndProduct(User user, Product product);
}
