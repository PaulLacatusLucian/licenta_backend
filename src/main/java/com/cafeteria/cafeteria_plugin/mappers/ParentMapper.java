package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.ParentDTO;
import com.cafeteria.cafeteria_plugin.models.Parent;
import org.springframework.stereotype.Component;

@Component
public class ParentMapper {

    public ParentDTO toDto(Parent parent) {
        ParentDTO dto = new ParentDTO();
        dto.setId(parent.getId());
        dto.setEmail(parent.getEmail());
        dto.setUsername(parent.getUsername());
        dto.setMotherName(parent.getMotherName());
        dto.setMotherEmail(parent.getMotherEmail());
        dto.setMotherPhoneNumber(parent.getMotherPhoneNumber());
        dto.setFatherName(parent.getFatherName());
        dto.setFatherEmail(parent.getFatherEmail());
        dto.setFatherPhoneNumber(parent.getFatherPhoneNumber());
        return dto;
    }

    public Parent toEntity(ParentDTO dto) {
        Parent parent = new Parent();
        parent.setId(dto.getId());
        parent.setEmail(dto.getEmail());
        parent.setUsername(dto.getUsername());
        parent.setMotherName(dto.getMotherName());
        parent.setMotherEmail(dto.getMotherEmail());
        parent.setMotherPhoneNumber(dto.getMotherPhoneNumber());
        parent.setFatherName(dto.getFatherName());
        parent.setFatherEmail(dto.getFatherEmail());
        parent.setFatherPhoneNumber(dto.getFatherPhoneNumber());
        return parent;
    }
}
