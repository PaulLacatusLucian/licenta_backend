package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

@Data
public class ParentDTO {
    private Long id;
    private String email;
    private String username;
    private String motherName;
    private String motherEmail;
    private String motherPhoneNumber;
    private String fatherName;
    private String fatherEmail;
    private String fatherPhoneNumber;
    private String profileImage;
}
