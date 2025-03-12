package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "students")
public class Student extends User {

    @Column(nullable = false)
    private String name;

    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class studentClass;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Parent parent;
}
