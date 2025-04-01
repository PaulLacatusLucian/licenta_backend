package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.OrderHistoryDTO;
import com.cafeteria.cafeteria_plugin.dtos.ParentDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.models.OrderHistory;
import org.springframework.stereotype.Component;

@Component
public class OrderHistoryMapper {

    public OrderHistoryDTO toDto(OrderHistory order) {
        OrderHistoryDTO dto = new OrderHistoryDTO();
        dto.setId(order.getId());
        dto.setMenuItemName(order.getMenuItemName());
        dto.setPrice(order.getPrice());
        dto.setQuantity(order.getQuantity());
        dto.setOrderTime(order.getOrderTime());

        var student = order.getStudent();
        if (student != null) {
            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setId(student.getId());
            studentDTO.setUsername(student.getUsername());
            studentDTO.setName(student.getName());
            studentDTO.setPhoneNumber(student.getPhoneNumber());
            if (student.getStudentClass() != null) {
                studentDTO.setClassName(student.getStudentClass().getName());
                studentDTO.setClassSpecialization(student.getStudentClass().getSpecialization());
                if (student.getStudentClass().getClassTeacher() != null) {
                    studentDTO.setClassTeacher(TeacherMapper.toDto(student.getStudentClass().getClassTeacher()));
                }
            }
            dto.setStudent(studentDTO);
        }

        var parent = order.getParent();
        if (parent != null) {
            ParentDTO parentDTO = new ParentDTO();
            parentDTO.setId(parent.getId());
            parentDTO.setUsername(parent.getUsername());
            parentDTO.setEmail(parent.getEmail());
            parentDTO.setMotherName(parent.getMotherName());
            parentDTO.setMotherEmail(parent.getMotherEmail());
            parentDTO.setMotherPhoneNumber(parent.getMotherPhoneNumber());
            parentDTO.setFatherName(parent.getFatherName());
            parentDTO.setFatherEmail(parent.getFatherEmail());
            parentDTO.setFatherPhoneNumber(parent.getFatherPhoneNumber());
            dto.setParent(parentDTO);
        }

        return dto;
    }
}
