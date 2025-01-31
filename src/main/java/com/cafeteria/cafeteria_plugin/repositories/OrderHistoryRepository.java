package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.OrderHistory;
import com.cafeteria.cafeteria_plugin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findAllByUserAndOrderTimeBetween(User user, LocalDateTime start, LocalDateTime end);

    List<OrderHistory> findByStudentId(Long id);

    List<OrderHistory> findByParentIdAndStudentId(Long id, Long id1);
}
