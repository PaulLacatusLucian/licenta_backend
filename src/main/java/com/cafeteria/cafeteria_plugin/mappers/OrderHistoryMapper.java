package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.OrderHistoryDTO;
import com.cafeteria.cafeteria_plugin.dtos.ParentDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.models.OrderHistory;
import org.springframework.stereotype.Component;

/**
 * Mapper-Komponente für die Konvertierung zwischen OrderHistory-Entitäten und OrderHistoryDTO-Objekten.
 * @author Paul Lacatus
 * @version 1.0
 * @see OrderHistory
 * @see OrderHistoryDTO
 * @see StudentDTO
 * @see ParentDTO
 * @since 2025-04-01
 */
@Component
public class OrderHistoryMapper {

    /**
     * Konvertiert eine OrderHistory-Entität zu einem umfassenden OrderHistoryDTO.
     * @param order OrderHistory-Entität mit vollständigen Benutzer-Beziehungen
     * @return OrderHistoryDTO mit aggregierten Benutzer- und Transaktionsdaten
     * @throws IllegalArgumentException wenn order null ist
     */
    public OrderHistoryDTO toDto(OrderHistory order) {
        OrderHistoryDTO dto = new OrderHistoryDTO();
        dto.setId(order.getId());
        dto.setMenuItemName(order.getMenuItemName());
        dto.setPrice(order.getPrice());
        dto.setQuantity(order.getQuantity());
        dto.setOrderTime(order.getOrderTime());

        // Student-DTO-Erstellung für Empfänger-Informationen
        var student = order.getStudent();
        if (student != null) {
            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setId(student.getId());
            studentDTO.setUsername(student.getUsername());
            studentDTO.setName(student.getName());
            studentDTO.setPhoneNumber(student.getPhoneNumber());

            // Klassen-Kontext für organisatorische Zuordnung
            if (student.getStudentClass() != null) {
                studentDTO.setClassName(student.getStudentClass().getName());
                studentDTO.setClassSpecialization(student.getStudentClass().getSpecialization());

                // Klassenlehrer-Integration für vollständigen Schulkontext
                if (student.getStudentClass().getClassTeacher() != null) {
                    studentDTO.setClassTeacher(TeacherMapper.toDto(student.getStudentClass().getClassTeacher()));
                }
            }
            dto.setStudent(studentDTO);
        }

        // Parent-DTO-Erstellung für Besteller-/Zahlungsinformationen
        var parent = order.getParent();
        if (parent != null) {
            ParentDTO parentDTO = new ParentDTO();
            parentDTO.setId(parent.getId());
            parentDTO.setUsername(parent.getUsername());
            parentDTO.setEmail(parent.getEmail());

            // Vollständige Kontaktinformationen beider Elternteile
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