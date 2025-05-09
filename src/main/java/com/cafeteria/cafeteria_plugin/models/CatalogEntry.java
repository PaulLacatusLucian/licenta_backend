package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
@Data
public class CatalogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "catalog_id")
    @JsonBackReference
    private Catalog catalog;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Enumerated(EnumType.STRING)
    private EntryType type; // GRADE sau ABSENCE

    private String subject;

    private Double gradeValue; // Pentru note

    private Boolean justified; // Pentru absențe

    private LocalDateTime date = LocalDateTime.now();

    private Long absenceId;
}

