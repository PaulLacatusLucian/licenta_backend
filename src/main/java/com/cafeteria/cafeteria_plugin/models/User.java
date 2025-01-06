package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED) // Strategia JOINED creează o tabelă separată pentru fiecare subclasă
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String userType; // Tipul de utilizator: "student", "parent", "teacher"
}
