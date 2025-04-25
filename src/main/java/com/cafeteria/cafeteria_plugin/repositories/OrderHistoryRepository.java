package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.OrderHistory;
import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

    List<OrderHistory> findAllByParentAndOrderTimeBetween(Parent parent, LocalDateTime start, LocalDateTime end);

    List<OrderHistory> findAllByStudentAndOrderTimeBetween(Student student, LocalDateTime start, LocalDateTime end);

    @Query("SELECT oh.menuItemName as name, COUNT(oh) as count " +
            "FROM OrderHistory oh GROUP BY oh.menuItemName ORDER BY count DESC LIMIT :limit")
    List<Map<String, Object>> findMostOrderedItems(@Param("limit") int limit);

    @Query("SELECT SUM(oh.price) FROM OrderHistory oh WHERE oh.orderTime BETWEEN :start AND :end")
    double sumOrderPricesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT FUNCTION('DATE', oh.orderTime) as date, SUM(oh.price) as total " +
            "FROM OrderHistory oh WHERE oh.orderTime BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('DATE', oh.orderTime) ORDER BY date")
    List<Map<String, Object>> findDailySalesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT s.name as name, COUNT(oh) as orderCount " +
            "FROM OrderHistory oh JOIN oh.student s " +
            "GROUP BY s.id ORDER BY orderCount DESC LIMIT :limit")
    List<Map<String, Object>> findStudentsWithMostOrders(@Param("limit") int limit);
}
