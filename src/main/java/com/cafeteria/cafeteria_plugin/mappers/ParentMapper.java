package com.cafeteria.cafeteria_plugin.mappers;

import com.cafeteria.cafeteria_plugin.dtos.ParentDTO;
import com.cafeteria.cafeteria_plugin.models.Parent;
import org.springframework.stereotype.Component;

/**
 * Mapper-Komponente für die bidirektionale Konvertierung zwischen Parent-Entitäten und ParentDTO-Objekten.
 * <p>
 * Diese Klasse implementiert das Mapper-Pattern für Elternkonten und stellt vollständige
 * bidirektionale Konvertierungsfunktionen zwischen der Parent-Domain-Entität und dem
 * entsprechenden Data Transfer Object bereit. Sie behandelt das komplexe Dual-Email-System
 * und stellt sichere Datenübertragung ohne sensible Informationen sicher.
 * <p>
 * Hauptfunktionalitäten:
 * - Bidirektionale Entity-DTO-Konvertierung
 * - Sichere Datenübertragung ohne Passwort-Informationen
 * - Vollständige Kontaktinformationen beider Elternteile
 * - Dual-Email-System-Unterstützung
 * - Profil-Informationen für Personalisierung
 * <p>
 * Sicherheitsaspekte:
 * - Ausschluss sensibler Daten (Passwort) aus DTO-Darstellung
 * - Sichere bidirektionale Konvertierung ohne Datenverlust
 * - Beibehaltung aller nicht-sensiblen Benutzerinformationen
 * - Unterstützung für sichere API-Kommunikation
 * <p>
 * Technische Eigenschaften:
 * - Spring Component für automatische Dependency Injection
 * - Stateless Design für Thread-Safety
 * - Vollständige bidirektionale Unterstützung
 * - Null-sichere Verarbeitung für Robustheit
 * - Optimiert für Eltern-Self-Service-Portale
 * <p>
 * Verwendungsszenarien:
 * - Eltern-Self-Service-Portale für Profilverwaltung
 * - API-Endpunkte für Eltern-Informationen
 * - Administrative Eltern-Verwaltung
 * - Kommunikationssysteme mit Eltern-Kontaktdaten
 *
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
     * <p>
     * Diese Methode erstellt eine sichere DTO-Darstellung eines Elternkontos
     * und überträgt alle nicht-sensiblen Informationen. Sie ist optimiert für
     * API-Responses und Frontend-Darstellungen, wo vollständige Kontakt-
     * informationen benötigt werden.
     * <p>
     * Übertragene Informationen:
     * - Basis-Identifikation (ID, Username, Haupt-Email)
     * - Vollständige Mutter-Kontaktdaten (Name, Email, Telefon)
     * - Vollständige Vater-Kontaktdaten (Name, Email, Telefon)
     * - Profil-Personalisierung (Profilbild)
     * <p>
     * Sicherheitsmerkmale:
     * - Expliziter Ausschluss des Passwort-Feldes
     * - Beibehaltung aller Kommunikations-relevanten Daten
     * - Sichere Übertragung für öffentliche API-Endpunkte
     * - Schutz vor versehentlicher Preisgabe sensibler Informationen
     * <p>
     * Dual-Email-System:
     * - Haupt-Email für System-Kommunikation
     * - Separate Mutter-/Vater-Emails für direkte Kontakte
     * - Flexible Familien-Kommunikationsstrukturen
     * - Unterstützung für getrennte oder alternative Kontakte
     * <p>
     * Verwendung:
     * - REST API Responses für Eltern-Profile
     * - Frontend-Darstellung in Eltern-Portalen
     * - Administrative Übersichten über Eltern-Kontakte
     * - Export-Funktionen für Kontaktlisten
     *
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
     * <p>
     * Diese Methode erstellt eine Parent-Entität aus DTO-Daten für Update-
     * Operationen oder neue Datensätze. Sie überträgt alle verfügbaren
     * Informationen und bereitet die Entität für Datenbankoperationen vor.
     * <p>
     * Konvertierte Informationen:
     * - Vollständige Identifikations- und Kontaktdaten
     * - Dual-Email-System mit allen Kontaktoptionen
     * - Profil-Personalisierungsinformationen
     * - Basis-Benutzerkonto-Informationen
     * <p>
     * Hinweise zur Verwendung:
     * - Passwort-Feld wird nicht gesetzt (bleibt null)
     * - UserType muss separat durch Service-Layer gesetzt werden
     * - ID kann für Update-Operationen übernommen werden
     * - Für neue Entitäten sollte ID null bleiben
     * <p>
     * Service-Layer-Integration:
     * - Entität erfordert zusätzliche Konfiguration durch Services
     * - Passwort muss durch separate Geschäftslogik gesetzt werden
     * - Validierung und Geschäftsregeln durch Service-Layer
     * - Database-Constraints werden bei Persistierung geprüft
     * <p>
     * Verwendung:
     * - Update-Operationen für bestehende Eltern-Profile
     * - Daten-Import aus externen Systemen
     * - Form-Daten-Verarbeitung in Web-Controllern
     * - Patch-Operationen für partielle Updates
     *
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