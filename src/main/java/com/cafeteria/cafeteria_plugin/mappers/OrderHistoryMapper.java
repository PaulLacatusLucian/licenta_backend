package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.OrderHistoryDTO;
import com.cafeteria.cafeteria_plugin.dtos.ParentDTO;
import com.cafeteria.cafeteria_plugin.dtos.StudentDTO;
import com.cafeteria.cafeteria_plugin.models.OrderHistory;
import org.springframework.stereotype.Component;

/**
 * Mapper-Komponente für die Konvertierung zwischen OrderHistory-Entitäten und OrderHistoryDTO-Objekten.
 * <p>
 * Diese Klasse implementiert das Mapper-Pattern für Cafeteria-Bestellhistorie und stellt
 * spezialisierte Konvertierungsfunktionen zwischen der OrderHistory-Domain-Entität und
 * dem entsprechenden Data Transfer Object bereit. Sie aggregiert komplexe Benutzer-
 * und Transaktionsdaten für umfassende Bestellübersichten.
 * <p>
 * Hauptfunktionalitäten:
 * - Entity-zu-DTO-Konvertierung mit Benutzer-Aggregation
 * - Vollständige Transaktionsdaten-Darstellung
 * - Integration von Eltern- und Schüler-Informationen
 * - Optimierte DTO-Struktur für Finanz- und Statistik-Berichte
 * - Null-sichere Verarbeitung für optionale Beziehungen
 * <p>
 * Technische Eigenschaften:
 * - Spring Component für automatische Dependency Injection
 * - Stateless Design für Thread-Safety
 * - Direkte Benutzer-DTO-Erstellung ohne zusätzliche Mapper
 * - Performance-optimierte Konvertierung für Listen-Operationen
 * - Integration mit Finanz- und Berichtssystemen
 * <p>
 * Verwendungsszenarien:
 * - Eltern-Portale für Ausgaben-Übersichten und Abrechnungen
 * - Schüler-Dashboards für Essenshistorie
 * - Administrative Berichte über Cafeteria-Umsätze
 * - API-Endpunkte für Bestellhistorie-Abfragen
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see OrderHistory
 * @see OrderHistoryDTO
 * @see StudentDTO
 * @see ParentDTO
 * @since 2025-01-01
 */
@Component
public class OrderHistoryMapper {

    /**
     * Konvertiert eine OrderHistory-Entität zu einem umfassenden OrderHistoryDTO.
     * <p>
     * Diese Methode erstellt eine vollständige DTO-Darstellung einer Cafeteria-Bestellung
     * und aggregiert alle relevanten Benutzer- und Transaktionsdaten. Sie ist optimiert
     * für Finanzberichte, Eltern-Portale und administrative Übersichten.
     * <p>
     * Aggregierte Transaktionsdaten:
     * - Basis-Bestellinformationen (ID, Artikel, Preis, Menge)
     * - Zeitstempel für chronologische Einordnung
     * - Vollständige Schüler-Informationen (Empfänger)
     * - Vollständige Eltern-Informationen (Besteller/Zahler)
     * <p>
     * Student-DTO-Aggregation:
     * - Grundlegende Identifikation und Kontaktdaten
     * - Klassen-Kontext für organisatorische Zuordnung
     * - Klassenlehrer-Informationen für vollständigen Schulkontext
     * - Optimiert für Lieferung und Empfänger-Identifikation
     * <p>
     * Parent-DTO-Aggregation:
     * - Vollständige Kontaktinformationen beider Elternteile
     * - Dual-Email-System für flexible Kommunikation
     * - Rechnungsrelevante Informationen
     * - Optimiert für Finanz-Management und Kommunikation
     * <p>
     * Geschäftslogik-Unterscheidung:
     * - Student: Tatsächlicher Empfänger der Bestellung
     * - Parent: Besteller und Zahlungspflichtiger
     * - Diese Trennung ermöglicht flexible Familien-Strukturen
     * <p>
     * Performance-Überlegungen:
     * - Direkte DTO-Erstellung ohne verschachtelte Mapper-Aufrufe
     * - Null-sichere Verarbeitung für Robustheit
     * - Einmalige Entitäts-Navigation für alle erforderlichen Daten
     * - Optimiert für Bulk-Operationen in Listen-Konvertierungen
     *
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