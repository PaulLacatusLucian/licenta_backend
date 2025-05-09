package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AbsenceDTO {
    private Long id;
    private StudentDTO student;
    private Long classSessionId;
    private LocalDateTime sessionDate;
    private TeacherDTO teacherWhoMarkedAbsence;
    private Boolean justified;
}