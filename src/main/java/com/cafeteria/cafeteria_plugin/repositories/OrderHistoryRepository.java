package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findAllByOrderTimeBetween(LocalDateTime start, LocalDateTime end);
}
