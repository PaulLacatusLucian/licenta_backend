package com.cafeteria.cafeteria_plugin.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbsenceDTO {
    private Long id;
    private StudentDTO student;

    private Long classSessionId;
    private String subject;
    private String className;
    private LocalDateTime date;

    private TeacherDTO teacherWhoMarkedAbsence;
    private Boolean justified;
}