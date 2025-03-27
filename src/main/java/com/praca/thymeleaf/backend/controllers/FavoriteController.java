package com.praca.thymeleaf.backend.controllers;

import com.praca.thymeleaf.backend.models.Favorite;
import com.praca.thymeleaf.backend.models.Product;
import com.praca.thymeleaf.backend.services.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "http://localhost:5173")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Product>> getFavorites(@PathVariable Long userId) {
        List<Product> favoriteProducts = favoriteService.getUserFavorites(userId);
        return ResponseEntity.ok(favoriteProducts);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToFavorites(@RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        Long productId = payload.get("productId");

        if (userId == null || productId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Brak wymaganych danych"));
        }

        boolean added = favoriteService.addToFavorites(userId, productId);
        if (added) {
            return ResponseEntity.ok(Map.of("message", "Dodano do ulubionych!"));
        } else {
            return ResponseEntity.status(409).body(Map.of("error", "Produkt już jest w ulubionych lub nie istnieje!"));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromFavorites(@RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        Long productId = payload.get("productId");

        if (userId == null || productId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Brak wymaganych danych"));
        }

        boolean removed = favoriteService.removeFromFavorites(userId, productId);
        if (removed) {
            return ResponseEntity.ok(Map.of("message", "Usunięto z ulubionych!"));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Produkt nie był w ulubionych!"));
        }
    }
}
