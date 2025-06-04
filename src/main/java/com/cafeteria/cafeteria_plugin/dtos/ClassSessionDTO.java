package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClassSessionDTO {
    private Long id;
    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TeacherDTO teacher;
    private List<AbsenceDTO> absences;
    private List<GradeDTO> grades;
    private String scheduleDay;
    private String className;
}
