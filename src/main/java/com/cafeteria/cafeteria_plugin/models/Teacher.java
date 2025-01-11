package com.cafeteria.cafeteria_plugin.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Teacher extends User {

    private String name;
    private String subject;

    @JsonBackReference
    @OneToOne(mappedBy = "classTeacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private Class classAsTeacher;

}
