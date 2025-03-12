package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.Chef;
import com.cafeteria.cafeteria_plugin.services.ChefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chefs")
public class ChefController {

    private final ChefService chefService;

    @Autowired
    public ChefController(ChefService chefService) {
        this.chefService = chefService;
    }

    // ✅ Creare bucătar (doar ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createChef(@RequestBody Chef chef) {
        try {
            Chef createdChef = chefService.createChef(chef);
            return ResponseEntity.ok(createdChef);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ Obținere toți bucătarii (doar ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Chef>> getAllChefs() {
        return ResponseEntity.ok(chefService.getAllChefs());
    }

    // ✅ Obținere bucătar după ID (ADMIN și CHEF)
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getChefById(@PathVariable Long id) {
        return chefService.getChefById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Actualizare bucătar (doar ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateChef(@PathVariable Long id, @RequestBody Chef chefDetails) {
        try {
            Chef updatedChef = chefService.updateChef(id, chefDetails);
            return ResponseEntity.ok(updatedChef);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ✅ Ștergere bucătar (doar ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChef(@PathVariable Long id) {
        try {
            chefService.deleteChef(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
