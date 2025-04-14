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

    @Autowired
    private ChefService chefService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Chef>> getAllChefs() {
        return ResponseEntity.ok(chefService.getAllChefs());
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getChefById(@PathVariable Long id) {
        return chefService.getChefById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


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
