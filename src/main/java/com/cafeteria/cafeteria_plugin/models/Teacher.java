package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Numele profesorului
    private String subject; // Materia predată

    @OneToMany(mappedBy = "teacher")
    private List<Schedule> schedules; // Orarul predat de profesor (legat de Schedule)
}

