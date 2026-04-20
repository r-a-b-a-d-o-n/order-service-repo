package com.example.orderservice.service;

import com.example.common.enums.SagaStatus;
import com.example.common.events.SagaEvent;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderStatus;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OutboxEvent;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper mapper;

    @Transactional
    public void  createOrder(OrderRequest request) throws Exception {
        // 1. Save the Order
        Order order = orderRepo.save(new Order(null, request.productCode(), request.quantity(), request.amount(), OrderStatus.PENDING.name()));

        // 2. Prepare the Event
        SagaEvent event = SagaEvent.create(null,
                order.getId(),
                order.getProductCode(),
                order.getQuantity(),
                order.getAmount(),
                SagaStatus.ORDER_PENDING
        );
        String json = mapper.writeValueAsString(event);

        // 3. Save to Outbox (Same Transaction!)
        outboxRepo.save(new OutboxEvent(json));
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void cleanupZombies() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(15);

        // 1. Find orders stuck in PENDING for too long
        List<Order> stuckOrders = orderRepo.findAllByStatusAndCreatedAtBefore("PENDING", cutoff);

        for (Order order : stuckOrders) {
            // 2. Mark as Cancelled
            order.setStatus("TIMEOUT_CANCELLED");
            orderRepo.save(order);

            // 3. Create an Outbox entry to notify other services to rollback!
            SagaEvent rollback = SagaEvent.create(null,
                    order.getId(), order.getProductCode(), order.getQuantity(),
                    order.getAmount(), SagaStatus.ORDER_CANCELLED
            );

            outboxRepo.save(new OutboxEvent(mapper.writeValueAsString(rollback)));
        }
    }
    @Scheduled(cron = "0 0 * * * *") // Run every hour
    public void deleteOldProcessedEvents() {
        // Delete events that are processed AND older than 24 hours
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        outboxRepo.deleteByProcessedTrueAndCreatedAtBefore(dayAgo);
    }
}