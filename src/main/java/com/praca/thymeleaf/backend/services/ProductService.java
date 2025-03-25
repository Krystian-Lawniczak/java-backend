package com.praca.thymeleaf.backend.services;

import com.praca.thymeleaf.backend.models.Product;
import com.praca.thymeleaf.backend.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // Konstruktor z wstrzykiwaniem zależności
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Pobierz wszystkie produkty
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Dodaj nowy produkt
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Znajdź produkt po ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Usuń produkt po ID
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    public List<Product> getRandomProducts(int count) {
        List<Product> all = productRepository.findAll();
        Collections.shuffle(all);
        return all.stream().limit(count).toList();
    }


}
