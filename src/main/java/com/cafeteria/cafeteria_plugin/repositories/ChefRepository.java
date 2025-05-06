package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.Chef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChefRepository extends JpaRepository<Chef, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
