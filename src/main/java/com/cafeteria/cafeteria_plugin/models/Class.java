package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;

@Data
@Entity
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne
    @JsonManagedReference
    @JoinColumn(name = "teacher_id", referencedColumnName = "id", unique = true)
    @EqualsAndHashCode.Exclude // Exclude pentru a preveni ciclicitatea
    private Teacher classTeacher;

    private String specialization;

    @OneToMany(mappedBy = "studentClass")
    @JsonManagedReference
    private List<Schedule> schedules;
}

