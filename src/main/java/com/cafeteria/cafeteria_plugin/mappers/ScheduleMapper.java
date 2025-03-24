package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.ScheduleDTO;
import com.cafeteria.cafeteria_plugin.dtos.TeacherDTO;
import com.cafeteria.cafeteria_plugin.models.Schedule;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper {

    public ScheduleDTO toDto(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setScheduleDay(schedule.getScheduleDay());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setSubjects(schedule.getSubjects());

        TeacherDTO teacherDTO = new TeacherDTO();
        teacherDTO.setId(schedule.getTeacher().getId());
        teacherDTO.setName(schedule.getTeacher().getName());
        teacherDTO.setUsername(schedule.getTeacher().getUsername());
        teacherDTO.setEmail(schedule.getTeacher().getEmail());
        teacherDTO.setSubject(schedule.getTeacher().getSubject());
        dto.setTeacher(teacherDTO);

        return dto;
    }
}
