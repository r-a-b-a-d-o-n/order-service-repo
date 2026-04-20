package com.example.orderservice.publisher;

import com.example.orderservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {
    public static final String ORDER_CREATED = "order-created";
    private final OutboxRepository outboxRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000) // Poll every second
    public void publish() {
        var events = outboxRepo.findByProcessedFalse();
        events.forEach(event -> {
            kafkaTemplate.send(ORDER_CREATED, event.getPayload());
            event.setProcessed(true);
            outboxRepo.save(event);
        });
    }


}