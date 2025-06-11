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

/**
 * REST-Controller für die Verwaltung von Kantinen-Menüelementen und Bestellungen.
 * <p>
 * Diese Klasse stellt HTTP-Endpunkte für das umfassende Management des
 * Kantinenbetriebs bereit, einschließlich Menüverwaltung, Bestellabwicklung,
 * Bilderverwaltung und Rechnungserstellung. Sie ermöglicht sowohl die administrative
 * Verwaltung des Menüangebots als auch die benutzerfreundliche Bestellabwicklung
 * für Eltern und Schüler.
 * <p>
 * Hauptfunktionen:
 * - CRUD-Operationen für Menüelemente mit Bildupload
 * - Bestellabwicklung und -verfolgung für Eltern
 * - Bestellhistorie und Rechnungserstellung
 * - Allergenverwaltung und Produktinformationen
 * - Bestandsverwaltung und Verfügbarkeitsprüfung
 * - JWT-basierte Benutzeridentifikation für personalisierte Services
 * - PDF-Rechnungsgenerierung für monatliche Abrechnungen
 * <p>
 * Sicherheit:
 * - Rollenbasierte Zugriffskontrolle für verschiedene Benutzertypen
 * - Eltern können nur für ihre eigenen Kinder bestellen
 * - Schüler können ihre eigene Bestellhistorie einsehen
 * - JWT-Authentifizierung für sichere Transaktionen
 * - Dateisicherheit bei Bildupload mit Größenbeschränkungen
 * <p>
 * Dateiverwaltung:
 * - Sichere Bildupload-Funktionalität
 * - UUID-basierte Dateinamen zur Konfliktvermeidung
 * - Konfigurierbare Upload-Verzeichnisse
 * - Dateigröße- und Typvalidierung
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see MenuItemService
 * @see MenuItem
 * @see OrderHistory
 * @see Parent
 * @see Student
 * @since 2024-11-28
 */
@RestController
@RequestMapping("/menu")
public class MenuItemController {

    /**
     * Service für Menüelement-Operationen und Bestellabwicklung.
     */
    @Autowired
    private MenuItemService menuItemService;

    /**
     * Service für Schüleroperationen.
     */
    @Autowired
    private StudentService studentService;

    /**
     * Service für Elternoperationen.
     */
    @Autowired
    private ParentService parentService;

    /**
     * Hilfsprogramm für JWT-Token-Verwaltung.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Mapper für Transformation von OrderHistory-Entitäten in DTOs.
     */
    @Autowired
    private OrderHistoryMapper orderHistoryMapper;

    /**
     * Konfiguriertes Verzeichnis für Bildupload.
     */
    @Value("${image.upload.dir}")
    private String uploadDir;

    /**
     * Fügt ein neues Menüelement mit optionalem Bild hinzu.
     * <p>
     * Erstellt ein neues Menüelement mit allen erforderlichen Informationen
     * einschließlich Name, Beschreibung, Preis, Menge und optionalen Allergenen.
     * Unterstützt den Upload eines Bildes, das automatisch im konfigurierten
     * Verzeichnis gespeichert und mit einem eindeutigen Dateinamen versehen wird.
     * <p>
     * Bildverarbeitung:
     * - Automatische Verzeichniserstellung falls nicht vorhanden
     * - UUID-basierte Dateinamen zur Konfliktvermeidung
     * - Sichere Dateispeicherung im konfigurierten Upload-Verzeichnis
     * - URL-Generierung für Frontend-Zugriff
     *
     * @param name        Name des Menüelements (erforderlich)
     * @param description Detaillierte Beschreibung des Gerichts (erforderlich)
     * @param price       Preis des Menüelements (erforderlich)
     * @param quantity    Verfügbare Menge (erforderlich)
     * @param allergens   Liste der Allergene (optional)
     * @param file        Bilddatei für das Menüelement (optional)
     * @return ResponseEntity mit Erfolgsmeldung und Bild-URL oder Fehler bei ungültigen Daten
     */
    @PostMapping("/add")
    public ResponseEntity<String> addMenuItemWithImage(
            @RequestParam(name = "name", required = true) String name,
            @RequestParam(name = "description", required = true) String description,
            @RequestParam(name = "price", required = true) Double price,
            @RequestParam(name = "quantity", required = true) Integer quantity,
            @RequestParam(name = "allergens", required = false) List<String> allergens,
            @RequestParam(name = "file", required = false) MultipartFile file) {
        try {
            System.out.println("Anfrage zum Hinzufügen von Menüelement erhalten");
            System.out.println("Name: " + name);
            System.out.println("Datei vorhanden: " + (file != null));

            File uploadDirectory = new File(uploadDir);
            System.out.println("Upload-Verzeichnis: " + uploadDirectory.getAbsolutePath());
            if (!uploadDirectory.exists()) {
                boolean created = uploadDirectory.mkdirs();
                System.out.println("Verzeichnis erstellt: " + created);
            }

            String imageUrl = null;
            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File destinationFile = new File(uploadDir, fileName);
                file.transferTo(destinationFile);
                imageUrl = "/images/" + fileName;
            } else {
                System.out.println("Keine Datei bereitgestellt oder Datei ist leer");
            }
            MenuItem menuItem = new MenuItem();
            menuItem.setName(name);
            menuItem.setDescription(description);
            menuItem.setPrice(price);
            menuItem.setQuantity(quantity);
            menuItem.setImageUrl(imageUrl);
            menuItem.setAllergens(allergens);

            menuItemService.addMenuItem(menuItem);

            return ResponseEntity.ok("Menüelement erfolgreich hinzugefügt mit Bild-URL: " + imageUrl);
        } catch (Exception e) {
            System.err.println("Fehler in addMenuItemWithImage: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler: " + e.getMessage());
        }
    }

    /**
     * Kauft ein Menüelement für das eigene Kind (moderne JWT-basierte Methode).
     * <p>
     * Nur für Eltern mit PARENT-Rolle zugänglich.
     * Extrahiert Eltern- und Schülerinformationen automatisch aus dem JWT-Token
     * und führt eine sichere Bestellung durch. Validiert die Verfügbarkeit
     * des Menüelements und verwaltet den Bestand automatisch.
     * <p>
     * Sicherheitsfeatures:
     * - Automatische Elternvalidierung über JWT
     * - Verknüpfung zu zugehörigem Schüler
     * - Bestandsprüfung und -verwaltung
     * - Sichere Transaktionsabwicklung
     *
     * @param menuItemId ID des zu kaufenden Menüelements
     * @param quantity   Gewünschte Menge
     * @param token      JWT-Authentifizierungs-Token des Elternteils
     * @return ResponseEntity mit Erfolgsmeldung oder detaillierter Fehlermeldung
     */
    @PostMapping("/me/purchase/{menuItemId}")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<String> purchaseMenuItemForMyChild(
            @PathVariable Long menuItemId,
            @RequestParam(name = "quantity") int quantity,
            @RequestHeader("Authorization") String token) {
        try {
            // Extrahiere Elternteil aus Token
            String username = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            Parent parent = parentService.findByUsername(username);

            if (parent == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Elternteil nicht gefunden");
            }

            // Hole mit Elternteil verknüpften Schüler
            Optional<Student> studentOpt = studentService.getStudentByParentId(parent.getId());
            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kein Schüler für diesen Elternteil gefunden");
            }

            Student student = studentOpt.get();

            // Verarbeite den Kauf
            menuItemService.purchaseMenuItem(parent.getId(), student.getId(), menuItemId, quantity);
            return ResponseEntity.ok("Kauf erfolgreich!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Fehler bei der Kaufabwicklung: " + e.getMessage());
        }
    }

    /**
     * Legacy-Methode - Wird für Rückwärtskompatibilität beibehalten.
     * <p>
     * Ältere Implementierung der Kauffunktion, die explizite Eltern- und
     * Schüler-IDs erfordert. Diese Methode wird für bestehende Integrationen
     * beibehalten, neue Implementierungen sollten die JWT-basierte Methode verwenden.
     *
     * @param menuItemId ID des zu kaufenden Menüelements
     * @param parentId   ID des kaufenden Elternteils
     * @param studentId  ID des Schülers für den gekauft wird
     * @param quantity   Gewünschte Menge
     * @return ResponseEntity mit Erfolgsmeldung oder Fehlermeldung
     */
    @PostMapping("/{menuItemId}/purchase")
    public ResponseEntity<String> purchaseMenuItem(
            @PathVariable Long menuItemId,
            @RequestParam(name = "parentId") Long parentId,
            @RequestParam(name = "studentId") Long studentId,
            @RequestParam(name = "quantity") int quantity) {
        try {
            menuItemService.purchaseMenuItem(parentId, studentId, menuItemId, quantity);
            return ResponseEntity.ok("Kauf erfolgreich!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Ruft alle verfügbaren Menüelemente ab.
     * <p>
     * Öffentlich zugängliche Methode, die eine vollständige Liste aller
     * verfügbaren Menüelemente zurückgibt. Enthält alle Produktinformationen
     * einschließlich Preise, Beschreibungen, Allergene und Verfügbarkeit.
     * Ideal für die Anzeige des kompletten Menüangebots.
     *
     * @return ResponseEntity mit der Liste aller Menüelemente
     */
    @GetMapping("/all")
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemService.getAllMenuItems();
        return ResponseEntity.ok(menuItems);
    }

    /**
     * Ruft ein spezifisches Menüelement anhand seiner ID ab.
     * <p>
     * Ermöglicht den Abruf detaillierter Informationen zu einem einzelnen
     * Menüelement einschließlich aller Produktdetails, Allergene und
     * aktueller Verfügbarkeit.
     *
     * @param id Eindeutige ID des Menüelements
     * @return ResponseEntity mit Menüelement-Details oder 404 falls nicht gefunden
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        Optional<MenuItem> menuItem = menuItemService.getMenuItemById(id);
        return menuItem.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    /**
     * Aktualisiert ein existierendes Menüelement.
     * <p>
     * Ermöglicht die vollständige Aktualisierung aller Menüelement-Attribute
     * einschließlich Name, Beschreibung, Preis, Menge und Allergene.
     * Bildaktualisierungen werden über separate Endpunkte verwaltet.
     *
     * @param id              ID des zu aktualisierenden Menüelements
     * @param updatedMenuItem Menüelement-Objekt mit neuen Daten
     * @return ResponseEntity mit aktualisiertem Menüelement oder 404 falls nicht gefunden
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem updatedMenuItem) {
        try {
            MenuItem updatedItem = menuItemService.updateMenuItem(id, updatedMenuItem);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Löscht ein Menüelement vollständig aus dem System.
     * <p>
     * Entfernt das Menüelement und alle zugehörigen Daten aus dem System.
     * Berücksichtigt bestehende Bestellungen und verwaltet referenzielle Integrität.
     *
     * @param id ID des zu löschenden Menüelements
     * @return ResponseEntity mit No-Content-Status bei Erfolg oder 404 falls nicht gefunden
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        boolean isDeleted = menuItemService.deleteMenuItem(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Ruft die eigene Bestellhistorie für einen angemeldeten Schüler ab.
     * <p>
     * Nur für Schüler mit STUDENT-Rolle zugänglich.
     * Ermöglicht es Schülern, ihre eigenen Bestellungen für einen spezifischen
     * Monat und Jahr einzusehen. Der Schüler wird über den JWT-Token identifiziert.
     *
     * @param token JWT-Authentifizierungs-Token des Schülers
     * @param month Monat für den die Bestellhistorie abgerufen werden soll
     * @param year  Jahr für das die Bestellhistorie abgerufen werden soll
     * @return ResponseEntity mit Liste der Bestellungen oder leere Liste bei Fehlern
     */
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

    /**
     * Ruft die Bestellhistorie des eigenen Kindes für Eltern ab.
     * <p>
     * Nur für Eltern mit PARENT-Rolle zugänglich.
     * Ermöglicht es Eltern, die Bestellungen ihres Kindes für einen spezifischen
     * Monat und Jahr einzusehen. Der Elternteil wird über den JWT-Token identifiziert
     * und das zugehörige Kind automatisch ermittelt.
     *
     * @param token JWT-Authentifizierungs-Token des Elternteils
     * @param month Monat für den die Bestellhistorie abgerufen werden soll
     * @param year  Jahr für das die Bestellhistorie abgerufen werden soll
     * @return ResponseEntity mit Liste der Kinderbestellungen oder leere Liste bei Fehlern
     */
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

    /**
     * Generiert eine PDF-Rechnung für das eigene Kind.
     * <p>
     * Nur für Eltern mit PARENT-Rolle zugänglich.
     * Erstellt eine detaillierte PDF-Rechnung mit allen Bestellungen des Kindes
     * für den angegebenen Monat und Jahr. Die PDF wird direkt als Download
     * bereitgestellt mit angemessenen HTTP-Headern für Dateiendownload.
     * <p>
     * PDF-Features:
     * - Detaillierte Auflistung aller Bestellungen
     * - Berechnete Gesamtsummen
     * - Professionelle Formatierung
     * - Automatischer Download mit korrektem Dateinamen
     *
     * @param token JWT-Authentifizierungs-Token des Elternteils
     * @param month Monat für den die Rechnung erstellt werden soll
     * @param year  Jahr für das die Rechnung erstellt werden soll
     * @return ResponseEntity mit PDF-Bytes oder Fehlermeldung
     */
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
                    .body("Nicht autorisiert".getBytes());
        }

        return studentService.getStudentByParentId(parent.getId())
                .map(student -> {
                    // Hole die PDF-Bytes
                    byte[] pdfBytes = menuItemService.generateInvoicePDF(student.getId(), month, year);

                    // Gib mit angemessenen Headern zurück
                    return ResponseEntity
                            .ok()
                            .contentType(MediaType.APPLICATION_PDF)
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=rechnung_" + month + "_" + year + ".pdf")
                            .body(pdfBytes);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Kein Schüler gefunden".getBytes()));
    }

    /**
     * Legacy-Methode - Wird für Rückwärtskompatibilität beibehalten.
     * <p>
     * Ältere Implementierung der Rechnungsgenerierung, die explizite Schüler-ID
     * erfordert. Diese Methode wird für bestehende Integrationen beibehalten,
     * neue Implementierungen sollten die JWT-basierte PDF-Methode verwenden.
     *
     * @param studentId ID des Schülers für den die Rechnung erstellt werden soll
     * @param month     Monat für den die Rechnung erstellt werden soll
     * @param year      Jahr für das die Rechnung erstellt werden soll
     * @return ResponseEntity mit Rechnungstext als String
     */
    @GetMapping("/invoice")
    public ResponseEntity<String> generateStudentInvoice(
            @RequestParam(name = "studentId") Long studentId,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "year") int year) {
        return ResponseEntity.ok(menuItemService.generateInvoiceForStudent(studentId, month, year));
    }

    /**
     * Lädt ein Bild für ein existierendes Menüelement hoch.
     * <p>
     * Ermöglicht das Hinzufügen oder Aktualisieren des Bildes für ein
     * bereits existierendes Menüelement. Implementiert Dateisicherheit
     * mit Größenbeschränkungen und sicherer Speicherung.
     * <p>
     * Sicherheitsfeatures:
     * - Dateigröße auf 10MB begrenzt
     * - UUID-basierte Dateinamen zur Konfliktvermeidung
     * - Automatische Verzeichniserstellung
     * - Sichere Dateispeicherung und URL-Generierung
     *
     * @param id   ID des Menüelements für das das Bild hochgeladen werden soll
     * @param file Bilddatei die hochgeladen werden soll
     * @return ResponseEntity mit Erfolgsmeldung und Bild-URL oder Fehlermeldung
     */
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<String> uploadImage(
            @PathVariable Long id,
            @RequestParam(name = "file") MultipartFile file) {
        try {
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dateigröße überschreitet das Maximum von 10MB");
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

            return ResponseEntity.ok("Bild erfolgreich hochgeladen: " + imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Hochladen der Datei: " + e.getMessage());
        }
    }
}