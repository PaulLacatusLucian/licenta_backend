package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

@Data
public class StudentDTO {
    private Long id;
    private String username;
    private String name;
    private String phoneNumber;

    private String className;
    private String classSpecialization;
    private TeacherDTO classTeacher;
}
