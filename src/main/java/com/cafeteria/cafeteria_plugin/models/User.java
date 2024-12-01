package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Numele de utilizator trebuie să fie unic
    private String username;

    @Column(nullable = false) // Parola nu poate fi null
    private String password;

    private boolean isEmployee;
}
