package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parents")
public class Parent extends User {

    @Column(nullable = false)
    private String motherName;

    @Column(unique = true)
    private String motherEmail;

    private String motherPhoneNumber;

    private String fatherName;

    @Column(unique = true)
    private String fatherEmail;

    private String fatherPhoneNumber;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Student> students;

}
