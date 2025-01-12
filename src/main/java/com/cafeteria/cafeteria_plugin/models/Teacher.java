package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
public class Teacher extends User {
    private String name;
    private String subject;

    @JsonBackReference
    @OneToOne(mappedBy = "classTeacher", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude // Exclude pentru a preveni ciclicitatea
    private Class classAsTeacher;
}

