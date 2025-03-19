package com.praca.thymeleaf.backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // Zmieniamy nazwę tabeli na "orders"
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user; // Użytkownik, który złożył zamówienie

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>(); // Lista produktów w zamówieniu

    private LocalDateTime orderDate; // Data zamówienia

    private double totalPrice; // Całkowita cena zamówienia

    private String status; // Status zamówienia: "PENDING", "COMPLETED", "CANCELLED"

    @Column(name = "is_cart") // 🔥 Dodano mapowanie na bazę danych
    private Boolean isCart = true; // Domyślnie każde zamówienie zaczyna jako koszyk

    // 🔹 Konstruktor
    public Order() {
        this.isCart = true; // Domyślnie każde zamówienie jest koszykiem
        this.status = "PENDING";
        this.orderDate = LocalDateTime.now();
    }

    // 🔹 Metoda obliczająca całkowitą cenę zamówienia
    public double calculateTotalPrice() {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
    }

    // 🔹 Gettery i Settery
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        this.totalPrice = calculateTotalPrice(); // Automatyczna aktualizacja ceny
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalPrice() {
        return calculateTotalPrice(); // Zawsze zwraca aktualną cenę
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsCart() {
        return isCart;
    }

    public void setIsCart(Boolean isCart) {
        this.isCart = isCart;
    }
}
