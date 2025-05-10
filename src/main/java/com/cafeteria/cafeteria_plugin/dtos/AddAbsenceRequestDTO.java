package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

@Data
public class AddAbsenceRequestDTO {
    private Long studentId;
    private Long classSessionId;
    private Boolean justified;
}

