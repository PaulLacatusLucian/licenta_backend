package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

@Data
public class EmailMessageDTO {
    private String teacherEmail;
    private String subject;
    private String content;
}