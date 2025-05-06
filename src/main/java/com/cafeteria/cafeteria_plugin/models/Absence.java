package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "class_session_id", nullable = false)
    private ClassSession classSession;
}


