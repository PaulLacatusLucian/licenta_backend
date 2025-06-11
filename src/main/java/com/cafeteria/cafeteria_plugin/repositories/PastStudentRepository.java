package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.PastStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository-Interface für absolventenspezifische Datenbankoperationen.
 * @author Paul Lacatus
 * @version 1.0
 * @see PastStudent
 * @see JpaRepository
 * @since 2025-01-15
 */
@Repository
public interface PastStudentRepository extends JpaRepository<PastStudent, Long> {
}