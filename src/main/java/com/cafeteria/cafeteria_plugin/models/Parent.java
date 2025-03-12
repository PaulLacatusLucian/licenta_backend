package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.*;

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
}
