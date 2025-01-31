package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String menuItemName;
    private Double price;
    private Integer quantity;
    private LocalDateTime orderTime;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false) // Părintele care face comanda
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false) // Elevul care primește comanda
    private Student student;
}
