package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.Chef;
import com.cafeteria.cafeteria_plugin.repositories.ChefRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Zentraler Service für die Köcheverwaltung im Cafeteria-System.
 *
 * Diese Klasse ist verantwortlich für:
 * - Erstellung und Verwaltung von Köche-Konten
 * - Validierung von Köche-Daten
 * - CRUD-Operationen für Köche
 * - Geschäftslogik für Köche-Verwaltung
 *
 * Der Service stellt sicher, dass alle Köche korrekt verwaltet werden
 * mit entsprechenden Validierungen und Eindeutigkeitsprüfungen.
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Chef
 * @since 2025-01-01
 */
@Service
public class ChefService {

    /**
     * Repository für Köche-Operationen.
     */
    private final ChefRepository chefRepository;

    /**
     * Konstruktor für ChefService.
     *
     * @param chefRepository Repository für Datenbankoperationen mit Köchen
     */
    @Autowired
    public ChefService(ChefRepository chefRepository) {
        this.chefRepository = chefRepository;
    }

    /**
     * Neuer Koch wird erstellt.
     *
     * Diese Methode führt Validierungen durch, um sicherzustellen,
     * dass Email und Benutzername eindeutig sind, bevor der Koch
     * in der Datenbank gespeichert wird.
     *
     * @param chef Der zu erstellende Koch
     * @return Der gespeicherte Koch mit generierter ID
     * @throws IllegalArgumentException Falls Email oder Benutzername bereits existiert
     */
    public Chef createChef(Chef chef) {
        if (chefRepository.existsByEmail(chef.getEmail())) {
            throw new IllegalArgumentException("Die E-Mail-Adresse wird bereits verwendet!");
        }
        if (chefRepository.existsByUsername(chef.getUsername())) {
            throw new IllegalArgumentException("Der Benutzername wird bereits verwendet!");
        }
        return chefRepository.save(chef);
    }

    /**
     * Alle Köche abrufen.
     *
     * @return Liste aller Köche im System
     */
    public List<Chef> getAllChefs() {
        return chefRepository.findAll();
    }

    /**
     * Koch nach ID abrufen.
     *
     * @param id Die ID des gesuchten Kochs
     * @return Optional mit dem gefundenen Koch oder leer falls nicht gefunden
     */
    public Optional<Chef> getChefById(Long id) {
        return chefRepository.findById(id);
    }

    /**
     * Koch nach ID löschen.
     *
     * Diese Methode überprüft zunächst, ob der Koch existiert,
     * bevor die Löschung durchgeführt wird.
     *
     * @param id Die ID des zu löschenden Kochs
     * @return true falls Löschung erfolgreich
     * @throws IllegalArgumentException Falls Koch mit der ID nicht existiert
     */
    public boolean deleteChef(Long id) {
        if (!chefRepository.existsById(id)) {
            throw new IllegalArgumentException("Koch mit der ID " + id + " existiert nicht!");
        }
        chefRepository.deleteById(id);
        return true;
    }

    /**
     * Koch aktualisieren.
     *
     * Diese Methode sucht den existierenden Koch und aktualisiert
     * nur die Felder, die in den neuen Daten angegeben sind.
     * Benutzername und Passwort werden nur aktualisiert, wenn sie
     * explizit angegeben werden.
     *
     * @param id Die ID des zu aktualisierenden Kochs
     * @param chefDetails Die neuen Daten für den Koch
     * @return Der aktualisierte Koch
     * @throws IllegalArgumentException Falls Koch mit der ID nicht gefunden wird
     */
    public Chef updateChef(Long id, Chef chefDetails) {
        return chefRepository.findById(id).map(existingChef -> {
            if (chefDetails.getName() != null) {
                existingChef.setName(chefDetails.getName());
            }

            // Benutzername und Passwort werden nur aktualisiert, wenn sie angegeben wurden
            if (chefDetails.getUsername() != null) {
                existingChef.setUsername(chefDetails.getUsername());
            }

            if (chefDetails.getPassword() != null) {
                existingChef.setPassword(chefDetails.getPassword());
            }

            return chefRepository.save(existingChef);
        }).orElseThrow(() -> new IllegalArgumentException("Koch mit der ID " + id + " wurde nicht gefunden."));
    }
}