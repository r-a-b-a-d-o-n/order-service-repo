package com.example.orderservice.repository;


import com.example.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Spring Data JPA derives the SQL from this name
    List<Order> findAllByStatusAndCreatedAtBefore(String status, LocalDateTime dateTime);

}

