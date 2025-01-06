package com.cafeteria.cafeteria_plugin.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Teacher extends User {

    private String name; // Numele profesorului
    private String subject; // Materia predată
}
