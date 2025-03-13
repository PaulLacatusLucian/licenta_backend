package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

@Data
public class TeacherDTO {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String subject;
}
