package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AbsenceDTO {
    private Long id;
    private StudentDTO student;
    private Long classSessionId;
}
