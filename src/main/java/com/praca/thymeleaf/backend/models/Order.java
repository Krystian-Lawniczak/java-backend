package com.praca.thymeleaf.backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders") // Zmieniamy nazwę tabeli na "orders"
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user; // Użytkownik, który złożył zamówienie

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items; // Lista produktów w zamówieniu

    private LocalDateTime orderDate; // Data zamówienia

    private double totalPrice; // Całkowita cena zamówienia

    private String status; // Status zamówienia: "PENDING", "COMPLETED", "CANCELLED"

    // Gettery i Settery
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
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
