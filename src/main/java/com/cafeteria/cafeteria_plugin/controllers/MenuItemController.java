package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.models.MenuItem;
import com.cafeteria.cafeteria_plugin.models.OrderHistory;
import com.cafeteria.cafeteria_plugin.services.MenuItemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/menu")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @Value("${image.upload.dir}")
    private String uploadDir;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    // ✅ Adaugă un produs în meniu cu imagine
    @PostMapping("/add")
    public ResponseEntity<String> addMenuItemWithImage(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "allergens", required = false) List<String> allergens,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            String imageUrl = null;
            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File destinationFile = new File(uploadDir + fileName);
                file.transferTo(destinationFile);
                imageUrl = "/images/" + fileName;
            }

            MenuItem menuItem = new MenuItem();
            menuItem.setName(name);
            menuItem.setDescription(description);
            menuItem.setPrice(price);
            menuItem.setQuantity(quantity);
            menuItem.setImageUrl(imageUrl);
            menuItem.setAllergens(allergens);

            menuItemService.addMenuItem(menuItem);
            return ResponseEntity.ok("Menu item added successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }

    // ✅ Părintele comandă mâncare pentru elev
    @PostMapping("/{menuItemId}/purchase")
    public ResponseEntity<String> purchaseMenuItem(
            @RequestParam Long parentId,
            @RequestParam Long studentId,
            @PathVariable Long menuItemId,
            @RequestParam int quantity) {
        try {
            menuItemService.purchaseMenuItem(parentId, studentId, menuItemId, quantity);
            return ResponseEntity.ok("Purchase successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ✅ Returnează toate produsele din meniu
    @GetMapping("/all")
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemService.getAllMenuItems();
        return ResponseEntity.ok(menuItems);
    }

    // ✅ Returnează un produs după ID
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        Optional<MenuItem> menuItem = menuItemService.getMenuItemById(id);
        return menuItem.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // ✅ Actualizează un produs
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem updatedMenuItem) {
        try {
            MenuItem updatedItem = menuItemService.updateMenuItem(id, updatedMenuItem);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // ✅ Șterge un produs
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        boolean isDeleted = menuItemService.deleteMenuItem(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // ✅ Returnează comenzile unui elev pentru o anumită lună și an
    @GetMapping("/orders/student/{studentId}/{month}/{year}")
    public ResponseEntity<List<OrderHistory>> getStudentOrders(
            @PathVariable Long studentId, @PathVariable int month, @PathVariable int year) {
        List<OrderHistory> orders = menuItemService.getOrderHistoryForStudent(studentId, month, year);
        return ResponseEntity.ok(orders);
    }

    // ✅ Returnează comenzile unui părinte pentru un elev într-o anumită lună și an
    @GetMapping("/orders/parent/{parentId}/{month}/{year}")
    public ResponseEntity<List<OrderHistory>> getParentOrders(
            @PathVariable Long parentId, @PathVariable int month, @PathVariable int year) {
        List<OrderHistory> orders = menuItemService.getOrderHistoryForParent(parentId, month, year);
        return ResponseEntity.ok(orders);
    }

    // ✅ Generează factura unui elev pentru o anumită lună și an
    @GetMapping("/{studentId}/invoice/{month}/{year}")
    public ResponseEntity<String> generateStudentInvoice(@PathVariable Long studentId, @PathVariable int month, @PathVariable int year) {
        return ResponseEntity.ok(menuItemService.generateInvoiceForStudent(studentId, month, year));
    }

    // ✅ Upload de imagine pentru un produs
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<String> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file.getSize() > 10 * 1024 * 1024) { // 10 MB
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds the maximum limit of 10MB");
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destinationFile = new File(uploadDir + fileName);
            file.transferTo(destinationFile);

            String imageUrl = "/images/" + fileName;
            menuItemService.updateMenuItemImage(id, imageUrl);

            return ResponseEntity.ok("Image uploaded successfully: " + imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }
}
