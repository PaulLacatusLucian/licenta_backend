package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teachers")
public class Teacher extends User {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String subject;

    @JsonBackReference
    @OneToOne(mappedBy = "classTeacher")
    @ToString.Exclude
    private Class classAsTeacher;
}
