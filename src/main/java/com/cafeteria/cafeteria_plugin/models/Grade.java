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
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Studentul care are nota

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher; // Profesorul care a dat nota

    @ManyToOne
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester; // Semestrul în care a fost acordată nota

    private Double grade; // Nota obținută
}
