package com.praca.thymeleaf.backend.models;

import jakarta.persistence.*;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product; // Produkt w zamówieniu

    private int quantity; // Ilość produktu

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order; // Powiązane zamówienie

    private double price; // Cena jednostkowa produktu

    // 🔹 Konstruktor bezargumentowy
    public OrderItem() {}

    // 🔹 Konstruktor, który automatycznie ustawia cenę
    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.price = product.getPrice();
    }

    // 🔹 Obliczanie całkowitej ceny pozycji
    public double calculateTotalPrice() {
        return this.quantity * this.price;
    }

    // 🔹 Gettery i settery
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.price = product.getPrice(); // Aktualizujemy cenę przy zmianie produktu
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
