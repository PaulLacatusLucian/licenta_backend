package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Data  // Aceasta include deja getterele
@Entity
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double grade;

    @Getter
    @ManyToOne
    @JoinColumn(name = "class_session_id", nullable = false)
    @JsonIgnore
    private ClassSession classSession;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Getter
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    @JsonIgnore
    private Teacher teacher;

}
