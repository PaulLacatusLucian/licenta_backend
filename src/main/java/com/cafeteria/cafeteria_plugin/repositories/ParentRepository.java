package com.cafeteria.cafeteria_plugin.repositories;
import com.cafeteria.cafeteria_plugin.models.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {
}
