package com.cafeteria.cafeteria_plugin.email;

import lombok.Data;

/**
 * DTO für Kontaktformular-Nachrichten an die Schule.
 *
 * @author Paul Lacatus
 * @version 1.0
 * @since 2025-01-15
 */
@Data
public class ContactMessageDTO {
    /**
     * Name des Absenders
     */
    private String name;

    /**
     * E-Mail-Adresse des Absenders
     */
    private String email;

    /**
     * Betreff der Nachricht
     */
    private String subject;

    /**
     * Inhalt der Nachricht
     */
    private String message;
}