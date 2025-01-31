package com.cafeteria.cafeteria_plugin.dtos;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GradeDTO {
    private Double grade;
    private String teacherName;
    private String subject;
    private LocalDateTime dateReceived;
}
