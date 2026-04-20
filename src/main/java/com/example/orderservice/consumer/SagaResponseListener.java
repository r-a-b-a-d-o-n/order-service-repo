package com.example.orderservice.consumer;

import com.example.common.enums.SagaStatus;
import com.example.common.events.SagaEvent;
import com.example.orderservice.dto.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
@Service
@Slf4j
public class SagaResponseListener {

    public static final String INVENTORY_EVENTS = "inventory-events";
    public static final String ORDER_SAGA_GROUP = "order-saga-group";
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

        public SagaResponseListener(OrderRepository orderRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    // 1. Listen for Inventory Failures (Stock out)
    @KafkaListener(topics = INVENTORY_EVENTS, groupId = ORDER_SAGA_GROUP)
    public void handleInventoryEvents(String message) throws Exception {
        SagaEvent event = objectMapper.readValue(message, SagaEvent.class);

        if (SagaStatus.INVENTORY_REJECTED.equals(event.status())) {
            updateOrderStatus(event.traceId(), event.orderId(), OrderStatus.CANCELLED_OUT_OF_STOCK.name());
        }
    }

    // 2. Listen for Payment Success/Failure
    @KafkaListener(topics = "payment-events", groupId = "order-saga-group")
    public void handlePaymentEvents(String message) throws Exception {
        SagaEvent event = objectMapper.readValue(message, SagaEvent.class);

        updateOrderStatus(event.traceId(), event.orderId(),
                SagaStatus.PAYMENT_SUCCESS.equals(event.status()) ? OrderStatus.CONFIRMED.name() : OrderStatus.CANCELLED_PAYMENT_FAILED.name());
    }

    private void updateOrderStatus(String traceId, Long id, String status) {
        orderRepository.findById(id).ifPresent(o -> {
            o.setStatus(status);
            orderRepository.save(o);
            log.info("[TRACE: {}] Order {} status updated to {}",
                    traceId, id, status);        });
    }
}




////test
//    private final OrderRepository orderRepository;
//    private final ObjectMapper objectMapper;
//
//    public SagaResponseListener(OrderRepository orderRepository, ObjectMapper objectMapper) {
//        this.orderRepository = orderRepository;
//        this.objectMapper = objectMapper;
//    }
//
//    @KafkaListener(topics = "payment-events", groupId = "order-group")
//    public void consumePaymentResponse(String message) {
//        try {
//            // Match the field names you saw in the terminal!
//            JsonNode jsonNode = objectMapper.readTree(message);
//            Long orderId = jsonNode.get("orderId").asLong();
//            String status = jsonNode.get("status").asText();
//
//            log.info("Updating Order {} with status {}", orderId, status);
//
//            orderRepository.findById(orderId).ifPresent(order -> {
//                if ("SUCCESS".equals(status)) {
//                    order.setStatus("CONFIRMED");
//                } else {
//                    order.setStatus("CANCELLED");
//                }
//                orderRepository.save(order);
//            });
//        } catch (Exception e) {
//            log.error("Failed to update order status", e);
//        }
//    }
