package com.example.orderservice.service;

import com.example.orderservice.model.Order;
import com.example.orderservice.model.OutboxEvent;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper mapper;

    @Transactional
    public void  createOrder(Double amount) throws Exception {
        // 1. Save the Order
        Order order = orderRepo.save(new Order(null, amount, "PENDING"));

        // 2. Prepare the Event
        String json = mapper.writeValueAsString(Map.of("orderId", order.getId(), "amount", amount));

        // 3. Save to Outbox (Same Transaction!)
        outboxRepo.save(new OutboxEvent(json));
    }
}