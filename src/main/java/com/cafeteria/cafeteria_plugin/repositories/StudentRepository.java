package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Class;
import com.cafeteria.cafeteria_plugin.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository-Interface für schülerspezifische Datenbankoperationen.
 * <p>
 * Diese Schnittstelle erweitert JpaRepository und stellt spezialisierte
 * CRUD-Operationen für Schüler bereit. Sie enthält custom Queries für
 * komplexe Abfragen und Operationen, die über Standard-CRUD hinausgehen.
 * <p>
 * Das Repository unterstützt:
 * - Standard CRUD-Operationen für Schüler
 * - Eltern-Kind-Beziehungsmanagement
 * - Klassenbasierte Schülersuche
 * - Jahresübergang und Klassenverwaltung
 * - Sichere Löschoperationen mit Referenzbereinigung
 * <p>
 * Besondere Merkmale:
 * - Transaktionale Operationen für Datenintegrität
 * - Custom Queries für komplexe Geschäftslogik
 * - Effiziente Bulk-Operationen
 * - Benutzernambasierte Authentifizierung
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see Student
 * @see Class
 * @see Parent
 * @see JpaRepository
 * @since 2025-01-01
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * Sucht einen Schüler anhand der Eltern-ID.
     * <p>
     * Diese Methode ermöglicht es Eltern, ihr zugeordnetes Kind zu finden.
     * Wird hauptsächlich für Eltern-Self-Service-Funktionen verwendet.
     *
     * @param parentId Eindeutige ID des Elternteils
     * @return Optional mit dem gefundenen Schüler oder leer falls nicht zugeordnet
     */
    Optional<Student> findByParentId(Long parentId);

    /**
     * Löscht alle Schüler, die einem bestimmten Elternteil zugeordnet sind.
     * <p>
     * Diese modifizierende Query wird für Cascading-Deletes verwendet,
     * wenn ein Eltern-Account gelöscht wird. Die Transaktion stellt sicher,
     * dass die Operation atomisch erfolgt.
     *
     * @param parentId ID des Elternteils, dessen Kinder gelöscht werden sollen
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Student s WHERE s.parent.id = :parentId")
    void deleteByParentId(@Param("parentId") Long parentId);

    /**
     * Sucht alle Schüler einer bestimmten Klasse.
     * <p>
     * Diese Methode wird für Klassenverwaltung und Jahresübergang verwendet.
     * Sie ermöglicht es, alle Schüler einer Klasse für Batch-Operationen zu laden.
     *
     * @param currentClass Die Klasse, deren Schüler gesucht werden
     * @return Liste aller Schüler in der angegebenen Klasse
     */
    List<Student> findByStudentClass(Class currentClass);

    /**
     * Sucht Schüler anhand einer Liste von Klassen-IDs.
     * <p>
     * Diese Methode ermöglicht effiziente Bulk-Operationen über mehrere Klassen.
     * Wird hauptsächlich für Lehrer verwendet, die mehrere Klassen unterrichten.
     *
     * @param classIds Liste der Klassen-IDs
     * @return Liste aller Schüler in den angegebenen Klassen
     */
    List<Student> findByStudentClassIdIn(List<Long> classIds);

    /**
     * Sucht einen Schüler anhand seines Benutzernamens.
     * <p>
     * Diese Methode wird für JWT-basierte Authentifizierung verwendet,
     * um den Schüler-Kontext aus dem Token zu laden.
     *
     * @param username Eindeutiger Benutzername des Schülers
     * @return Der gefundene Schüler oder null falls nicht vorhanden
     */
    Student findByUsername(String username);

    /**
     * Sucht alle Schüler einer bestimmten Klasse anhand der Klassen-ID.
     * <p>
     * Diese Methode ist eine optimierte Version für ID-basierte Suchen
     * und wird für Performance-kritische Operationen verwendet.
     *
     * @param id Eindeutige ID der Klasse
     * @return Liste aller Schüler in der Klasse mit der angegebenen ID
     */
    List<Student> findByStudentClassId(Long id);
}