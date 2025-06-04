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

    // Neues Menüelement hinzufügen
    public MenuItem addMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    // Alle Menüelemente abrufen
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    // Menüelement nach ID abrufen
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id);
    }

    // Menüelement aktualisieren
    public MenuItem updateMenuItem(Long id, MenuItem updatedMenuItem) {
        return menuItemRepository.findById(id).map(existingMenuItem -> {
            existingMenuItem.setName(updatedMenuItem.getName());
            existingMenuItem.setDescription(updatedMenuItem.getDescription());
            existingMenuItem.setPrice(updatedMenuItem.getPrice());
            existingMenuItem.setQuantity(updatedMenuItem.getQuantity());
            return menuItemRepository.save(existingMenuItem);
        }).orElseThrow(() -> new IllegalArgumentException("Menüelement nicht gefunden."));
    }

    // Menüelement löschen
    public boolean deleteMenuItem(Long id) {
        if (menuItemRepository.existsById(id)) {
            menuItemRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Bild-URL eines Menüelements aktualisieren
    public void updateMenuItemImage(Long id, String imageUrl) {
        menuItemRepository.findById(id).ifPresent(menuItem -> {
            menuItem.setImageUrl(imageUrl);
            menuItemRepository.save(menuItem);
        });
    }

    // Bestellverlauf für einen Elternteil abrufen
    public List<OrderHistory> getOrderHistoryForParent(Long parentId, int month, int year) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Elternteil nicht gefunden."));
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.findAllByParentAndOrderTimeBetween(parent, start, end);
    }

    // Bestellverlauf für einen Schüler abrufen
    public List<OrderHistory> getOrderHistoryForStudent(Long studentId, int month, int year) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Schüler nicht gefunden."));
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.findAllByStudentAndOrderTimeBetween(student, start, end);
    }

    // Elternteil bestellt Essen für Schüler
    public void purchaseMenuItem(Long parentId, Long studentId, Long menuItemId, int quantity) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Elternteil nicht gefunden."));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Schüler nicht gefunden."));
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("Menüelement nicht gefunden."));

        if (menuItem.getQuantity() < quantity) {
            throw new IllegalArgumentException("Nicht genügend Lagerbestand für " + menuItem.getName());
        }

        menuItem.setQuantity(menuItem.getQuantity() - quantity);
        menuItemRepository.save(menuItem);

        OrderHistory order = new OrderHistory();
        order.setMenuItemName(menuItem.getName());
        order.setPrice(menuItem.getPrice() * quantity);
        order.setQuantity(quantity);
        order.setOrderTime(LocalDateTime.now());
        order.setParent(parent);
        order.setStudent(student);

        orderHistoryRepository.save(order);
    }

    // Textbasierte Rechnung generieren
    public String generateInvoiceForStudent(Long studentId, int month, int year) {
        List<OrderHistory> orders = getOrderHistoryForStudent(studentId, month, year);
        double total = orders.stream().mapToDouble(OrderHistory::getPrice).sum();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        StringBuilder invoice = new StringBuilder();
        invoice.append("Rechnung für ").append(month).append("/").append(year).append("\n");
        invoice.append("====================================\n");
        for (OrderHistory order : orders) {
            invoice.append(order.getMenuItemName())
                    .append(" x ").append(order.getQuantity())
                    .append(" = $").append(order.getPrice())
                    .append(" | Datum: ").append(order.getOrderTime().format(dateTimeFormatter))
                    .append("\n");
        }
        invoice.append("====================================\n");
        invoice.append("Gesamt: $").append(total).append("\n");

        return invoice.toString();
    }

    // Meistbestellte Artikel
    public List<Map<String, Object>> getMostPopularItems(int limit) {
        return orderHistoryRepository.findMostOrderedItems(limit);
    }

    // Gesamtumsatz für einen Zeitraum
    public double getTotalRevenueForPeriod(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.sumOrderPricesBetween(start, end);
    }

    // Tagesumsatz pro Monat
    public List<Map<String, Object>> getDailySalesForMonth(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.findDailySalesBetween(start, end);
    }

    // Schüler mit den meisten Bestellungen
    public List<Map<String, Object>> getTopStudentsByOrderCount(int limit) {
        return orderHistoryRepository.findStudentsWithMostOrders(limit);
    }

    // Lagerstatus abrufen
    public List<Map<String, Object>> getInventoryStatus() {
        List<MenuItem> items = menuItemRepository.findAll();
        return items.stream()
                .map(item -> {
                    Map<String, Object> status = new HashMap<>();
                    status.put("id", item.getId());
                    status.put("name", item.getName());
                    status.put("quantity", item.getQuantity());
                    status.put("status", item.getQuantity() > 10 ? "Auf Lager" :
                            (item.getQuantity() > 0 ? "Niedriger Bestand" : "Nicht verfügbar"));
                    return status;
                })
                .collect(Collectors.toList());
    }

    // Bestellungen nach Allergen gruppieren
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

    // PDF-Rechnung generieren
    public byte[] generateInvoicePDF(Long studentId, int month, int year) {
        try {
            List<OrderHistory> orders = getOrderHistoryForStudent(studentId, month, year);
            double total = orders.stream().mapToDouble(OrderHistory::getPrice).sum();

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Schüler nicht gefunden."));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("RECHNUNG", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            document.add(new Paragraph("Rechnung für: " + student.getName(), boldFont));
            document.add(new Paragraph("Monat: " + month + "/" + year, normalFont));
            document.add(new Paragraph("Erstellt am: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), normalFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            Stream.of("Artikel", "Menge", "Preis", "Datum")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle, boldFont));
                        table.addCell(header);
                    });

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            for (OrderHistory order : orders) {
                table.addCell(new Phrase(order.getMenuItemName(), normalFont));
                table.addCell(new Phrase(String.valueOf(order.getQuantity()), normalFont));
                table.addCell(new Phrase("$" + order.getPrice(), normalFont));
                table.addCell(new Phrase(order.getOrderTime().format(dateTimeFormatter), normalFont));
            }

            document.add(table);
            document.add(new Paragraph(" "));

            Paragraph totalParagraph = new Paragraph("Gesamt: $" + total, boldFont);
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParagraph);

            Paragraph footer = new Paragraph("Vielen Dank für Ihre Bestellung!", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Generieren der PDF-Rechnung: " + e.getMessage());
        }
    }
}
