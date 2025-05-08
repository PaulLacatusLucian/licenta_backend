package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class ClassSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject; // Materia pentru această sesiune

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    @JsonIgnore
    private Teacher teacher; // Profesorul care ține sesiunea

    @Column(nullable = false)
    private LocalDateTime startTime; // Ora de început

    @Column(nullable = false)
    private LocalDateTime endTime; // Ora de sfârșit

    @OneToMany(mappedBy = "classSession", cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonIgnore
    private List<Absence> absences; // Lista absențelor pentru această sesiune

    @OneToMany(mappedBy = "classSession", cascade = CascadeType.ALL)
    private List<Grade> grades; // Lista notelor acordate în această sesiune

    private String scheduleDay;

    private String className;
}
