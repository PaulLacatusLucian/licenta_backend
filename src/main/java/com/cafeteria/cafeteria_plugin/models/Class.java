package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String classTeacher;
    private String specialization;

    @OneToMany(mappedBy = "studentClass")
    @JsonManagedReference // Garantează serializarea unidirecțională
    private List<Schedule> schedules;
}
