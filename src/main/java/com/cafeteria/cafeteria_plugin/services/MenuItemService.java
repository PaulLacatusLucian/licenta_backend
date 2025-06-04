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

/**
 * Umfassender Service für das Cafeteria-Management im Schulverwaltungssystem.
 * <p>
 * Diese Klasse verwaltet alle Aspekte der Schul-Cafeteria:
 * - Menüverwaltung (CRUD-Operationen für Speisen und Getränke)
 * - Bestellsystem (Eltern bestellen für ihre Kinder)
 * - Lagerverwaltung (Bestandskontrolle und Verfügbarkeit)
 * - Rechnungsstellung (PDF-Generierung und Abrechnungen)
 * - Analytik (Verkaufsstatistiken und Berichte)
 * <p>
 * Hauptfunktionen:
 * - Vollständige Menüverwaltung mit Bildern und Allergenen
 * - Sichere Bestellabwicklung mit Bestandsvalidierung
 * - Automatische PDF-Rechnungsgenerierung
 * - Umfassende Verkaufs- und Popularitätsanalysen
 * - Integration mit Eltern- und Schülerdaten
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see MenuItem
 * @see OrderHistory
 * @see Parent
 * @see Student
 * @since 2025-01-01
 */
@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;

    /**
     * Konstruktor mit Dependency Injection für alle benötigten Repositories.
     */
    public MenuItemService(MenuItemRepository menuItemRepository,
                           OrderHistoryRepository orderHistoryRepository,
                           ParentRepository parentRepository,
                           StudentRepository studentRepository) {
        this.menuItemRepository = menuItemRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Fügt einen neuen Menüpunkt zur Cafeteria hinzu.
     *
     * @param menuItem Der neue Menüpunkt mit allen Informationen
     * @return Der gespeicherte Menüpunkt mit generierter ID
     */
    public MenuItem addMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    /**
     * Ruft alle verfügbaren Menüpunkte ab.
     *
     * @return Liste aller Menüpunkte in der Cafeteria
     */
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    /**
     * Ruft einen spezifischen Menüpunkt anhand seiner ID ab.
     *
     * @param id Eindeutige ID des Menüpunkts
     * @return Optional mit dem Menüpunkt oder leer falls nicht gefunden
     */
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id);
    }

    /**
     * Aktualisiert einen existierenden Menüpunkt.
     * <p>
     * Diese Methode überschreibt die Daten eines existierenden Menüpunkts
     * mit den bereitgestellten neuen Informationen.
     *
     * @param id              ID des zu aktualisierenden Menüpunkts
     * @param updatedMenuItem Menüpunkt mit neuen Daten
     * @return Der aktualisierte Menüpunkt
     * @throws IllegalArgumentException Falls der Menüpunkt nicht gefunden wird
     */
    public MenuItem updateMenuItem(Long id, MenuItem updatedMenuItem) {
        return menuItemRepository.findById(id).map(existingMenuItem -> {
            existingMenuItem.setName(updatedMenuItem.getName());
            existingMenuItem.setDescription(updatedMenuItem.getDescription());
            existingMenuItem.setPrice(updatedMenuItem.getPrice());
            existingMenuItem.setQuantity(updatedMenuItem.getQuantity());
            return menuItemRepository.save(existingMenuItem);
        }).orElseThrow(() -> new IllegalArgumentException("Menüpunkt nicht gefunden"));
    }

    /**
     * Löscht einen Menüpunkt aus der Cafeteria.
     *
     * @param id ID des zu löschenden Menüpunkts
     * @return true wenn erfolgreich gelöscht, false wenn nicht gefunden
     */
    public boolean deleteMenuItem(Long id) {
        if (menuItemRepository.existsById(id)) {
            menuItemRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Aktualisiert das Bild eines Menüpunkts.
     *
     * @param id       ID des Menüpunkts
     * @param imageUrl Neue Bild-URL
     */
    public void updateMenuItemImage(Long id, String imageUrl) {
        menuItemRepository.findById(id).ifPresent(menuItem -> {
            menuItem.setImageUrl(imageUrl);
            menuItemRepository.save(menuItem);
        });
    }

    /**
     * Ruft die Bestellhistorie für einen Elternteil ab.
     *
     * @param parentId ID des Elternteils
     * @param month    Monat für den Bericht
     * @param year     Jahr für den Bericht
     * @return Liste aller Bestellungen des Elternteils im angegebenen Zeitraum
     * @throws IllegalArgumentException Falls der Elternteil nicht gefunden wird
     */
    public List<OrderHistory> getOrderHistoryForParent(Long parentId, int month, int year) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Elternteil nicht gefunden"));
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.findAllByParentAndOrderTimeBetween(parent, start, end);
    }

    /**
     * Ruft die Bestellhistorie für einen Schüler ab.
     *
     * @param studentId ID des Schülers
     * @param month     Monat für den Bericht
     * @param year      Jahr für den Bericht
     * @return Liste aller Bestellungen für den Schüler im angegebenen Zeitraum
     * @throws IllegalArgumentException Falls der Schüler nicht gefunden wird
     */
    public List<OrderHistory> getOrderHistoryForStudent(Long studentId, int month, int year) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Schüler nicht gefunden"));
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.findAllByStudentAndOrderTimeBetween(student, start, end);
    }

    /**
     * Ermöglicht einem Elternteil, Essen für sein Kind zu bestellen.
     * <p>
     * Diese Methode führt umfassende Validierungen durch:
     * - Überprüfung der Existenz von Elternteil, Schüler und Menüpunkt
     * - Validierung der Bestandsverfügbarkeit
     * - Automatische Bestandsreduktion
     * - Erstellung der Bestellhistorie
     *
     * @param parentId   ID des bestellenden Elternteils
     * @param studentId  ID des Schülers, für den bestellt wird
     * @param menuItemId ID des bestellten Menüpunkts
     * @param quantity   Anzahl der bestellten Portionen
     * @throws IllegalArgumentException Falls Validierungen fehlschlagen
     */
    public void purchaseMenuItem(Long parentId, Long studentId, Long menuItemId, int quantity) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Elternteil nicht gefunden"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Schüler nicht gefunden"));
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("Menüpunkt nicht gefunden"));

        if (menuItem.getQuantity() < quantity) {
            throw new IllegalArgumentException("Nicht genügend Vorrat verfügbar für " + menuItem.getName());
        }

        // Bestand reduzieren
        menuItem.setQuantity(menuItem.getQuantity() - quantity);
        menuItemRepository.save(menuItem);

        // Bestellung erstellen
        OrderHistory order = new OrderHistory();
        order.setMenuItemName(menuItem.getName());
        order.setPrice(menuItem.getPrice() * quantity);
        order.setQuantity(quantity);
        order.setOrderTime(LocalDateTime.now());
        order.setParent(parent);
        order.setStudent(student);

        orderHistoryRepository.save(order);
    }

    /**
     * Generiert eine Textrechnung für einen Schüler.
     *
     * @param studentId ID des Schülers
     * @param month     Monat für die Rechnung
     * @param year      Jahr für die Rechnung
     * @return Textuelle Rechnung mit allen Bestellungen und Gesamtsumme
     */
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
                    .append(" = ").append(order.getPrice()).append("€")
                    .append(" | Datum: ").append(order.getOrderTime().format(dateTimeFormatter))
                    .append("\n");
        }
        invoice.append("====================================\n");
        invoice.append("Gesamtsumme: ").append(total).append("€\n");

        return invoice.toString();
    }

    /**
     * Ermittelt die beliebtesten Menüpunkte basierend auf Bestellhäufigkeit.
     *
     * @param limit Maximale Anzahl zurückzugebender Artikel
     * @return Liste der beliebtesten Artikel mit Namen und Bestellanzahl
     */
    public List<Map<String, Object>> getMostPopularItems(int limit) {
        return orderHistoryRepository.findMostOrderedItems(limit);
    }

    /**
     * Berechnet den Gesamtumsatz für einen bestimmten Zeitraum.
     *
     * @param month Monat für die Berechnung
     * @param year  Jahr für die Berechnung
     * @return Gesamtumsatz im angegebenen Monat
     */
    public double getTotalRevenueForPeriod(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.sumOrderPricesBetween(start, end);
    }

    /**
     * Ermittelt tägliche Verkaufszahlen für einen Monat.
     *
     * @param month Monat für die Analyse
     * @param year  Jahr für die Analyse
     * @return Liste mit täglichen Umsätzen
     */
    public List<Map<String, Object>> getDailySalesForMonth(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        return orderHistoryRepository.findDailySalesBetween(start, end);
    }

    /**
     * Ermittelt Schüler mit den meisten Bestellungen.
     *
     * @param limit Maximale Anzahl zurückzugebender Schüler
     * @return Liste der aktivsten Schüler mit Bestellanzahl
     */
    public List<Map<String, Object>> getTopStudentsByOrderCount(int limit) {
        return orderHistoryRepository.findStudentsWithMostOrders(limit);
    }

    /**
     * Erstellt einen Lagerbestandsbericht.
     *
     * @return Liste aller Menüpunkte mit Bestandsstatus
     */
    public List<Map<String, Object>> getInventoryStatus() {
        List<MenuItem> items = menuItemRepository.findAll();
        return items.stream()
                .map(item -> {
                    Map<String, Object> status = new HashMap<>();
                    status.put("id", item.getId());
                    status.put("name", item.getName());
                    status.put("quantity", item.getQuantity());
                    status.put("status", item.getQuantity() > 10 ? "Verfügbar" :
                            (item.getQuantity() > 0 ? "Niedrig" : "Ausverkauft"));
                    return status;
                })
                .collect(Collectors.toList());
    }

    /**
     * Analysiert Bestellungen nach Allergenen.
     *
     * @return Map mit Allergenen und deren Bestellhäufigkeit
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
     * Generiert eine PDF-Rechnung für einen Schüler.
     * <p>
     * Diese Methode erstellt eine professionelle PDF-Rechnung mit:
     * - Vollständiger Kopfzeile mit Schülerdaten
     * - Tabellarischer Auflistung aller Bestellungen
     * - Formatierter Gesamtsumme
     * - Professionellem Layout
     *
     * @param studentId ID des Schülers
     * @param month     Monat für die Rechnung
     * @param year      Jahr für die Rechnung
     * @return PDF-Rechnung als Byte-Array
     * @throws RuntimeException Falls PDF-Generierung fehlschlägt
     */
    public byte[] generateInvoicePDF(Long studentId, int month, int year) {
        try {
            List<OrderHistory> orders = getOrderHistoryForStudent(studentId, month, year);
            double total = orders.stream().mapToDouble(OrderHistory::getPrice).sum();

            // Schülerdaten abrufen
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Schüler nicht gefunden"));

            // PDF-Dokument erstellen
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);

            // Dokument öffnen
            document.open();

            // Titel hinzufügen
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("RECHNUNG", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Leerzeile

            // Rechnungsdetails hinzufügen
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            document.add(new Paragraph("Rechnung für: " + student.getName(), boldFont));
            document.add(new Paragraph("Monat: " + month + "/" + year, normalFont));
            document.add(new Paragraph("Erstellt am: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), normalFont));
            document.add(new Paragraph(" ")); // Leerzeile

            // Tabelle für Bestellungen erstellen
            PdfPTable table = new PdfPTable(4); // 4 Spalten
            table.setWidthPercentage(100);

            // Tabellen-Header hinzufügen
            Stream.of("Artikel", "Anzahl", "Preis", "Datum")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle, boldFont));
                        table.addCell(header);
                    });

            // Datenzeilen hinzufügen
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            for (OrderHistory order : orders) {
                table.addCell(new Phrase(order.getMenuItemName(), normalFont));
                table.addCell(new Phrase(String.valueOf(order.getQuantity()), normalFont));
                table.addCell(new Phrase(order.getPrice() + "€", normalFont));
                table.addCell(new Phrase(order.getOrderTime().format(dateTimeFormatter), normalFont));
            }

            document.add(table);
            document.add(new Paragraph(" ")); // Leerzeile

            // Gesamtsumme hinzufügen
            Paragraph totalParagraph = new Paragraph("Gesamtsumme: " + total + "€", boldFont);
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParagraph);

            // Fußzeile hinzufügen
            Paragraph footer = new Paragraph("Vielen Dank für Ihre Bestellung!", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            // Dokument schließen
            document.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler bei PDF-Rechnungsgenerierung: " + e.getMessage());
        }
    }
}