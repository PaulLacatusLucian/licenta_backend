package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderHistoryDTO {
    private Long id;
    private String menuItemName;
    private Double price;
    private int quantity;
    private LocalDateTime orderTime;
    private StudentDTO student;
    private ParentDTO parent;
}