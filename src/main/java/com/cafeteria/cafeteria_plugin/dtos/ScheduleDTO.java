package com.cafeteria.cafeteria_plugin.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ScheduleDTO {
    private Long id;
    private String scheduleDay;
    private String startTime;
    private String endTime;
    private List<String> subjects;
    private TeacherDTO teacher;
    private String className;

}
