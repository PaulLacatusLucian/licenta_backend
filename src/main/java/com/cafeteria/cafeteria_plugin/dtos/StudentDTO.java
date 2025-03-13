package com.cafeteria.cafeteria_plugin.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    private Long id;
    private String username;
    private String name;
    private String phoneNumber;

    private String className;
    private String classSpecialization;
    private TeacherDTO classTeacher;
}
