package com.praca.thymeleaf.backend.services;

import com.praca.thymeleaf.backend.models.Favorite;
import com.praca.thymeleaf.backend.models.User;
import com.praca.thymeleaf.backend.models.Product;
import com.praca.thymeleaf.backend.repositories.FavoriteRepository;
import com.praca.thymeleaf.backend.repositories.UserRepository;
import com.praca.thymeleaf.backend.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public boolean addToFavorites(Long userId, Long productId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Product> productOpt = productRepository.findById(productId);

        if (userOpt.isEmpty() || productOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        Product product = productOpt.get();


        if (favoriteRepository.existsByUserAndProduct(user, product)) {
            return false;
        }

        Favorite favorite = new Favorite(user, product);
        favoriteRepository.save(favorite);
        return true;
    }


    public boolean removeFromFavorites(Long userId, Long productId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Product> productOpt = productRepository.findById(productId);

        if (userOpt.isEmpty() || productOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        Product product = productOpt.get();

        Optional<Favorite> favoriteOpt = favoriteRepository.findByUserAndProduct(user, product);
        if (favoriteOpt.isEmpty()) {
            return false;
        }

        favoriteRepository.delete(favoriteOpt.get());
        return true;
    }

    public List<Product> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(Favorite::getProduct)
                .collect(Collectors.toList());
    }

}
