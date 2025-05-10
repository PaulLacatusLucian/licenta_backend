package com.cafeteria.cafeteria_plugin.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeDTO {
    private Long id;
    private Double grade;
    private String description;
    private Long studentId;
    private String studentName;
    private String teacherName;
    private String subject;
    private LocalDateTime sessionDate;
}
