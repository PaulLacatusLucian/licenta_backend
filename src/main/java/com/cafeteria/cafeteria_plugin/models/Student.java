package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private Class studentClass;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @Column
    private String profileImage;

}
