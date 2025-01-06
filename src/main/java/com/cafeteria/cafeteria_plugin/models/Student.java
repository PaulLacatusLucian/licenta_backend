package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Student extends User {

    private String name;
    private String email;
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class studentClass;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Parent parent;
}


