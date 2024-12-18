package com.cafeteria.cafeteria_plugin.models;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student; // Studentul care are absența

    private String subject; // Materia pentru care există absența
    private Integer count; // Numărul de absențe pentru această materie
}
