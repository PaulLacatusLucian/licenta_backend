package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phoneNumber; // Număr de telefon al părintelui
    private String email; // Email-ul părintelui

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student; // Părintele este asociat unui student
}
