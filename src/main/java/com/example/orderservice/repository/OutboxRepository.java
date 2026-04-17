package com.example.orderservice.repository;


import com.example.orderservice.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    // Finds messages that haven't been sent to Kafka yet
    List<OutboxEvent> findByProcessedFalse();
}