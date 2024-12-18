package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Numele clasei (ex: 10A)
    private String classTeacher; // Profesorul titular al clasei
    private String specialization; // Specializarea clasei
}
