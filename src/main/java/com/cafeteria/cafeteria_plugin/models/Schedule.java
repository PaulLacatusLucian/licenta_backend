package com.cafeteria.cafeteria_plugin.models;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;


import java.util.List;

@Data
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id")
    @JsonBackReference
    @ToString.Exclude
    private Class studentClass;

    private String scheduleDay;
    private String startTime;
    private String endTime;

    @ElementCollection
    private List<String> subjects;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
}
