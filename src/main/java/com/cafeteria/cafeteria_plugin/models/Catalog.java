package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Catalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "class_id")
    private Class studentClass;

    @OneToMany(mappedBy = "catalog", cascade = CascadeType.ALL)
    private List<CatalogEntry> entries = new ArrayList<>();

    // Getteri și setteri
}