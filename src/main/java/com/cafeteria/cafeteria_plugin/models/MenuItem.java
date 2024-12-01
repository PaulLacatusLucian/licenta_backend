package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Integer quantity;

    @ElementCollection
    private List<String> allergens;
}
