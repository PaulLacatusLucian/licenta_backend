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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
