package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class studentClass; // Referința către clasa în care este înscris studentul

    @OneToMany(mappedBy = "student")
    private List<Parent> parents;

    @OneToMany(mappedBy = "student")
    private List<Grade> grades;
}
