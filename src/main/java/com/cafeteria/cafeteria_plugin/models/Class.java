package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Teacher classTeacher;

    @OneToMany(mappedBy = "studentClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private List<Schedule> schedules;

    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    private String specialization;
}

