package com.praca.thymeleaf.backend.services;

import com.praca.thymeleaf.backend.models.Order;
import com.praca.thymeleaf.backend.models.OrderItem;
import com.praca.thymeleaf.backend.models.Product;
import com.praca.thymeleaf.backend.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class OrderServiceTest {

    @Test
    void createOrderShouldCalculateTotalPrice() {
        // Mockowanie OrderRepository
        OrderRepository mockRepository = Mockito.mock(OrderRepository.class);

        // Konfiguracja mocka, aby metoda save zwracała przekazany obiekt
        Mockito.when(mockRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Inicjalizacja OrderService z mockiem
        OrderService orderService = new OrderService(mockRepository);

        // Przygotowanie danych testowych
        Product product1 = new Product();
        product1.setPrice(100.0);

        Product product2 = new Product();
        product2.setPrice(200.0);

        OrderItem item1 = new OrderItem();
        item1.setProduct(product1);
        item1.setQuantity(2); // 2 x 100 = 200

        OrderItem item2 = new OrderItem();
        item2.setProduct(product2);
        item2.setQuantity(1); // 1 x 200 = 200

        Order testOrder = new Order();
        testOrder.setItems(List.of(item1, item2));

        // Wywołanie metody createOrder
        Order createdOrder = orderService.createOrder(testOrder);

        // Sprawdzenie, czy całkowita cena została obliczona poprawnie
        assertEquals(400.0, createdOrder.getTotalPrice());
    }
}
