package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.ParentDTO;
import com.cafeteria.cafeteria_plugin.models.Parent;
import org.springframework.stereotype.Component;

/**
 * Mapper-Komponente für die bidirektionale Konvertierung zwischen Parent-Entitäten und ParentDTO-Objekten.
 * @author Paul Lacatus
 * @version 1.0
 * @see Parent
 * @see ParentDTO
 * @see com.cafeteria.cafeteria_plugin.models.User
 * @since 2025-01-01
 */
@Component
public class ParentMapper {

    /**
     * Konvertiert eine Parent-Entität zu einem sicheren ParentDTO.
     * @param parent Parent-Entität mit vollständigen Informationen
     * @return ParentDTO ohne sensible Daten für sichere Übertragung
     * @throws IllegalArgumentException wenn parent null ist
     */
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
        dto.setProfileImage(parent.getProfileImage());
        return dto;
    }

    /**
     * Konvertiert ein ParentDTO zu einer Parent-Entität für Persistierung.
     * @param dto ParentDTO mit zu konvertierenden Daten
     * @return Parent-Entität bereit für Service-Layer-Verarbeitung
     * @throws IllegalArgumentException wenn dto null ist
     */
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
        parent.setProfileImage(dto.getProfileImage());
        return parent;
    }
}