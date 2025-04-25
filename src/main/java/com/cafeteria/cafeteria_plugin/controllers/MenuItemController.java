package com.cafeteria.cafeteria_plugin.controllers;

import com.cafeteria.cafeteria_plugin.dtos.OrderHistoryDTO;
import com.cafeteria.cafeteria_plugin.mappers.OrderHistoryMapper;
import com.cafeteria.cafeteria_plugin.models.MenuItem;
import com.cafeteria.cafeteria_plugin.models.OrderHistory;
import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.models.Student;
import com.cafeteria.cafeteria_plugin.security.JwtUtil;
import com.cafeteria.cafeteria_plugin.services.MenuItemService;
import com.cafeteria.cafeteria_plugin.services.ParentService;
import com.cafeteria.cafeteria_plugin.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ParentService parentService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OrderHistoryMapper orderHistoryMapper;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @PostMapping("/add")
    public ResponseEntity<String> addMenuItemWithImage(
            @RequestParam(name = "name", required = true) String name,
            @RequestParam(name = "description", required = true) String description,
            @RequestParam(name = "price", required = true) Double price,
            @RequestParam(name = "quantity", required = true) Integer quantity,
            @RequestParam(name = "allergens", required = false) List<String> allergens,
            @RequestParam(name = "file", required = false) MultipartFile file) {
        try {
            System.out.println("Add menu item request received");
            System.out.println("Name: " + name);
            System.out.println("File present: " + (file != null));

            File uploadDirectory = new File(uploadDir);
            System.out.println("Upload directory: " + uploadDirectory.getAbsolutePath());
            if (!uploadDirectory.exists()) {
                boolean created = uploadDirectory.mkdirs();
                System.out.println("Created directory: " + created);
            }

            String imageUrl = null;
            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File destinationFile = new File(uploadDir, fileName);
                file.transferTo(destinationFile);
                imageUrl = "/images/" + fileName;
            } else {
                System.out.println("No file provided or file is empty");
            }
            MenuItem menuItem = new MenuItem();
            menuItem.setName(name);
            menuItem.setDescription(description);
            menuItem.setPrice(price);
            menuItem.setQuantity(quantity);
            menuItem.setImageUrl(imageUrl);
            menuItem.setAllergens(allergens);

            menuItemService.addMenuItem(menuItem);

            return ResponseEntity.ok("Menu item added successfully with image URL: " + imageUrl);
        } catch (Exception e) {
            System.err.println("Error in addMenuItemWithImage: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * Purchase a menu item, extracting parent and student info from the token
     */
    @PostMapping("/me/purchase/{menuItemId}")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<String> purchaseMenuItemForMyChild(
            @PathVariable Long menuItemId,
            @RequestParam(name = "quantity") int quantity,
            @RequestHeader("Authorization") String token) {
        try {
            // Extract parent from token
            String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Parent parent = parentService.findByUsername(username);

            if (parent == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Parent not found");
            }

            // Get student associated with parent
            Optional<Student> studentOpt = studentService.getStudentByParentId(parent.getId());
            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No student found for this parent");
            }

            Student student = studentOpt.get();

            // Process the purchase
            menuItemService.purchaseMenuItem(parent.getId(), student.getId(), menuItemId, quantity);
            return ResponseEntity.ok("Purchase successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing purchase: " + e.getMessage());
        }
    }

    /**
     * Legacy method - maintained for backward compatibility
     */
    @PostMapping("/{menuItemId}/purchase")
    public ResponseEntity<String> purchaseMenuItem(
            @PathVariable Long menuItemId,
            @RequestParam(name = "parentId") Long parentId,
            @RequestParam(name = "studentId") Long studentId,
            @RequestParam(name = "quantity") int quantity) {
        try {
            menuItemService.purchaseMenuItem(parentId, studentId, menuItemId, quantity);
            return ResponseEntity.ok("Purchase successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemService.getAllMenuItems();
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        Optional<MenuItem> menuItem = menuItemService.getMenuItemById(id);
        return menuItem.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem updatedMenuItem) {
        try {
            MenuItem updatedItem = menuItemService.updateMenuItem(id, updatedMenuItem);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        boolean isDeleted = menuItemService.deleteMenuItem(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/orders/student/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<OrderHistoryDTO>> getMyStudentOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "year") int year) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Student student = studentService.findByUsername(username);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(List.of());
        }

        var rawOrders = menuItemService.getOrderHistoryForStudent(student.getId(), month, year);
        var dtos = rawOrders.stream()
                .map(orderHistoryMapper::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/me/child/orders")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<List<OrderHistoryDTO>> getChildOrdersForParent(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "year") int year) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Parent parent = parentService.findByUsername(username);
        if (parent == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(List.of());
        }

        return studentService.getStudentByParentId(parent.getId())
                .map(student -> {
                    var orders = menuItemService.getOrderHistoryForStudent(student.getId(), month, year)
                            .stream()
                            .map(orderHistoryMapper::toDto)
                            .toList();
                    return ResponseEntity.ok(orders);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of()));
    }

    @GetMapping("/me/invoice")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<byte[]> generateInvoiceForMyChild(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "year") int year) {
        String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Parent parent = parentService.findByUsername(username);
        if (parent == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Unauthorized".getBytes());
        }

        return studentService.getStudentByParentId(parent.getId())
                .map(student -> {
                    // Get the PDF bytes
                    byte[] pdfBytes = menuItemService.generateInvoicePDF(student.getId(), month, year);

                    // Return with appropriate headers
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.APPLICATION_PDF)
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=invoice_" + month + "_" + year + ".pdf")
                            .body(pdfBytes);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("No student found".getBytes()));
    }

    /**
     * Legacy method - maintained for backward compatibility
     */
    @GetMapping("/invoice")
    public ResponseEntity<String> generateStudentInvoice(
            @RequestParam(name = "studentId") Long studentId,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "year") int year) {
        return ResponseEntity.ok(menuItemService.generateInvoiceForStudent(studentId, month, year));
    }

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<String> uploadImage(
            @PathVariable Long id,
            @RequestParam(name = "file") MultipartFile file) {
        try {
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds the maximum limit of 10MB");
            }

            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destinationFile = new File(uploadDir, fileName);
            file.transferTo(destinationFile);

            String imageUrl = "/images/" + fileName;
            menuItemService.updateMenuItemImage(id, imageUrl);

            return ResponseEntity.ok("Image uploaded successfully: " + imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }
}