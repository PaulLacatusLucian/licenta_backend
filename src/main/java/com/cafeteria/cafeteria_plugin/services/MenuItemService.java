package com.cafeteria.cafeteria_plugin.services;

import com.cafeteria.cafeteria_plugin.models.*;
import com.cafeteria.cafeteria_plugin.repositories.MenuItemRepository;
import com.cafeteria.cafeteria_plugin.repositories.OrderHistoryRepository;
import com.cafeteria.cafeteria_plugin.repositories.ParentRepository;
import com.cafeteria.cafeteria_plugin.repositories.StudentRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;

    public MenuItemService(MenuItemRepository menuItemRepository,
                           OrderHistoryRepository orderHistoryRepository,
                           ParentRepository parentRepository,
                           StudentRepository studentRepository) {
        this.menuItemRepository = menuItemRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
    }

    // ✅ Adaugă un nou produs în meniu
    public MenuItem addMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    // ✅ Returnează toate produsele din meniu
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    // ✅ Returnează un produs după ID
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id);
    }

    // ✅ Actualizează un produs
    public MenuItem updateMenuItem(Long id, MenuItem updatedMenuItem) {
        return menuItemRepository.findById(id).map(existingMenuItem -> {
            existingMenuItem.setName(updatedMenuItem.getName());
            existingMenuItem.setDescription(updatedMenuItem.getDescription());
            existingMenuItem.setPrice(updatedMenuItem.getPrice());
            existingMenuItem.setQuantity(updatedMenuItem.getQuantity());
            return menuItemRepository.save(existingMenuItem);
        }).orElseThrow(() -> new IllegalArgumentException("MenuItem not found"));
    }

    // ✅ Șterge un produs
    public boolean deleteMenuItem(Long id) {
        if (menuItemRepository.existsById(id)) {
            menuItemRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ✅ Actualizează imaginea unui produs
    public void updateMenuItemImage(Long id, String imageUrl) {
        menuItemRepository.findById(id).ifPresent(menuItem -> {
            menuItem.setImageUrl(imageUrl);
            menuItemRepository.save(menuItem);
        });
    }

    // ✅ Istoricul comenzilor pentru un părinte
    public List<OrderHistory> getOrderHistoryForParent(Long parentId, int month, int year) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.findAllByParentAndOrderTimeBetween(parent, start, end);
    }

    // ✅ Istoricul comenzilor pentru un elev
    public List<OrderHistory> getOrderHistoryForStudent(Long studentId, int month, int year) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.findAllByStudentAndOrderTimeBetween(student, start, end);
    }

    // ✅ Permite unui părinte să comande mâncare pentru un elev
    public void purchaseMenuItem(Long parentId, Long studentId, Long menuItemId, int quantity) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("MenuItem not found"));

        if (menuItem.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available for " + menuItem.getName());
        }

        // Scade cantitatea din stoc
        menuItem.setQuantity(menuItem.getQuantity() - quantity);
        menuItemRepository.save(menuItem);

        // Creează comanda
        OrderHistory order = new OrderHistory();
        order.setMenuItemName(menuItem.getName());
        order.setPrice(menuItem.getPrice() * quantity);
        order.setQuantity(quantity);
        order.setOrderTime(LocalDateTime.now());
        order.setParent(parent);
        order.setStudent(student);

        orderHistoryRepository.save(order);
    }

    // ✅ Generează o factură pentru un elev
    public String generateInvoiceForStudent(Long studentId, int month, int year) {
        List<OrderHistory> orders = getOrderHistoryForStudent(studentId, month, year);
        double total = orders.stream().mapToDouble(OrderHistory::getPrice).sum();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        StringBuilder invoice = new StringBuilder();
        invoice.append("Invoice for ").append(month).append("/").append(year).append("\n");
        invoice.append("====================================\n");
        for (OrderHistory order : orders) {
            invoice.append(order.getMenuItemName())
                    .append(" x ").append(order.getQuantity())
                    .append(" = $").append(order.getPrice())
                    .append(" | Date: ").append(order.getOrderTime().format(dateTimeFormatter))
                    .append("\n");
        }
        invoice.append("====================================\n");
        invoice.append("Total: $").append(total).append("\n");

        return invoice.toString();
    }

    // Add these methods to your MenuItemService.java

    /**
     * Get most popular menu items based on order count
     */
    public List<Map<String, Object>> getMostPopularItems(int limit) {
        return orderHistoryRepository.findMostOrderedItems(limit);
    }

    /**
     * Get total revenue for a specific time period
     */
    public double getTotalRevenueForPeriod(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.sumOrderPricesBetween(start, end);
    }

    /**
     * Get daily sales for a specific month
     */
    public List<Map<String, Object>> getDailySalesForMonth(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.findDailySalesBetween(start, end);
    }

    /**
     * Get students with the most orders
     */
    public List<Map<String, Object>> getTopStudentsByOrderCount(int limit) {
        return orderHistoryRepository.findStudentsWithMostOrders(limit);
    }

    /**
     * Get current inventory status
     */
    public List<Map<String, Object>> getInventoryStatus() {
        List<MenuItem> items = menuItemRepository.findAll();
        return items.stream()
                .map(item -> {
                    Map<String, Object> status = new HashMap<>();
                    status.put("id", item.getId());
                    status.put("name", item.getName());
                    status.put("quantity", item.getQuantity());
                    status.put("status", item.getQuantity() > 10 ? "In Stock" :
                            (item.getQuantity() > 0 ? "Low Stock" : "Out of Stock"));
                    return status;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get orders count by allergen
     */
    public Map<String, Long> getOrderCountByAllergen() {
        List<OrderHistory> allOrders = orderHistoryRepository.findAll();
        return allOrders.stream()
                .flatMap(order -> {
                    Optional<MenuItem> menuItem = menuItemRepository.findByName(order.getMenuItemName());
                    if (menuItem.isPresent() && menuItem.get().getAllergens() != null) {
                        return menuItem.get().getAllergens().stream();
                    }
                    return Stream.empty();
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    /**
     * Generate a PDF invoice for a student
     */
    public byte[] generateInvoicePDF(Long studentId, int month, int year) {
        try {
            List<OrderHistory> orders = getOrderHistoryForStudent(studentId, month, year);
            double total = orders.stream().mapToDouble(OrderHistory::getPrice).sum();

            // Get student details
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));

            // Create PDF document
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);

            // Open document
            document.open();

            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("INVOICE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Add space

            // Add invoice details
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            document.add(new Paragraph("Invoice for: " + student.getName(), boldFont));
            document.add(new Paragraph("Month: " + month + "/" + year, normalFont));
            document.add(new Paragraph("Date Generated: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), normalFont));
            document.add(new Paragraph(" ")); // Add space

            // Create table for order items
            PdfPTable table = new PdfPTable(4); // 4 columns
            table.setWidthPercentage(100);

            // Add table headers
            Stream.of("Item", "Quantity", "Price", "Date")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle, boldFont));
                        table.addCell(header);
                    });

            // Add data rows
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            for (OrderHistory order : orders) {
                table.addCell(new Phrase(order.getMenuItemName(), normalFont));
                table.addCell(new Phrase(String.valueOf(order.getQuantity()), normalFont));
                table.addCell(new Phrase("$" + order.getPrice(), normalFont));
                table.addCell(new Phrase(order.getOrderTime().format(dateTimeFormatter), normalFont));
            }

            document.add(table);
            document.add(new Paragraph(" ")); // Add space

            // Add total
            Paragraph totalParagraph = new Paragraph("Total: $" + total, boldFont);
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParagraph);

            // Add footer
            Paragraph footer = new Paragraph("Thank you for your order!", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            // Close document
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate PDF invoice: " + e.getMessage());
        }
    }
}
