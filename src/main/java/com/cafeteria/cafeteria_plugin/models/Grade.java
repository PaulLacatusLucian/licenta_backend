package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id")
    private Student student; // Studentul care are nota

    private String subject; // Materia pentru care se dă nota
    private Double grade; // Nota obținută

    private String teacher; // Profesorul care a dat nota
}
