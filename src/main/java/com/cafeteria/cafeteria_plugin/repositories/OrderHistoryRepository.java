package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.OrderHistory;
import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

    List<OrderHistory> findAllByParentAndOrderTimeBetween(Parent parent, LocalDateTime start, LocalDateTime end);

    List<OrderHistory> findAllByStudentAndOrderTimeBetween(Student student, LocalDateTime start, LocalDateTime end);
}
