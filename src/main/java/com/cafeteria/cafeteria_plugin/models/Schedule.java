package com.cafeteria.cafeteria_plugin.models;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id")
    @JsonBackReference // Evită bucla de serializare
    private Class studentClass;

    private String scheduleDay; // Ziua săptămânii (ex: Luni, Marți, etc.)
    private String startTime; // Ora de început
    private String endTime; // Ora de sfârșit

    @ElementCollection
    private List<String> subjects; // Materiile din ziua respectivă

    @ManyToOne
    @JoinColumn(name = "teacher_id") // Creates a foreign key column in Schedule for teacher reference
    private Teacher teacher; // Adăugați referința către Teacher
}
