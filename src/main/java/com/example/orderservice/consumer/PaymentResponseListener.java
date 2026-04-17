package com.example.orderservice.consumer;

import com.example.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class PaymentResponseListener {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    public PaymentResponseListener(OrderRepository orderRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "payment-events", groupId = "order-group")
    public void consumePaymentResponse(String message) {
        try {
            // Match the field names you saw in the terminal!
            JsonNode jsonNode = objectMapper.readTree(message);
            Long orderId = jsonNode.get("orderId").asLong();
            String status = jsonNode.get("status").asText();

            log.info("Updating Order {} with status {}", orderId, status);

            orderRepository.findById(orderId).ifPresent(order -> {
                if ("SUCCESS".equals(status)) {
                    order.setStatus("CONFIRMED");
                } else {
                    order.setStatus("CANCELLED");
                }
                orderRepository.save(order);
            });
        } catch (Exception e) {
            log.error("Failed to update order status", e);
        }
    }
}