package com.cafeteria.cafeteria_plugin.repositories;

import com.cafeteria.cafeteria_plugin.models.OrderHistory;
import com.cafeteria.cafeteria_plugin.models.Parent;
import com.cafeteria.cafeteria_plugin.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Repository-Interface für bestellhistorienspezifische Datenbankoperationen.
 * @author Paul Lacatus
 * @version 1.0
 * @see OrderHistory
 * @see Parent
 * @see Student
 * @see JpaRepository
 * @since 2025-11-28
 */
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

    /**
     * Sucht alle Bestellungen eines Elternteils in einem bestimmten Zeitraum.
     * <p>
     * Diese Methode wird für Eltern-Self-Service-Portale und Abrechnungen verwendet.
     * Sie ermöglicht es Eltern, ihre Bestellhistorie einzusehen und
     * monatliche Abrechnungen zu generieren.
     * <p>
     * Verwendung:
     * - Monatliche Abrechnungen für Eltern
     * - Bestellhistorie im Elternportal
     * - Finanzielle Übersichten und Budgetplanung
     * - Rechnungsstellung und Zahlungsabwicklung
     *
     * @param parent Der Elternteil, dessen Bestellungen gesucht werden
     * @param start  Startzeit des Suchzeitraums (inklusive)
     * @param end    Endzeit des Suchzeitraums (inklusive)
     * @return Liste aller Bestellungen des Elternteils im Zeitraum
     */
    List<OrderHistory> findAllByParentAndOrderTimeBetween(Parent parent, LocalDateTime start, LocalDateTime end);

    /**
     * Sucht alle Bestellungen eines Schülers in einem bestimmten Zeitraum.
     * <p>
     * Diese Methode wird für schülerspezifische Übersichten und Analysen verwendet.
     * Sie ermöglicht es, das Essverhalten und die Präferenzen einzelner
     * Schüler zu analysieren.
     * <p>
     * Verwendung:
     * - Schüler-Dashboard und Essenshistorie
     * - Ernährungsanalysen und -beratung
     * - Allergiker-Tracking und Sicherheit
     * - Individuelle Berichte für Eltern
     *
     * @param student Der Schüler, dessen Bestellungen gesucht werden
     * @param start   Startzeit des Suchzeitraums (inklusive)
     * @param end     Endzeit des Suchzeitraums (inklusive)
     * @return Liste aller Bestellungen des Schülers im Zeitraum
     */
    List<OrderHistory> findAllByStudentAndOrderTimeBetween(Student student, LocalDateTime start, LocalDateTime end);

    /**
     * Ermittelt die am häufigsten bestellten Menüartikel.
     * <p>
     * Diese komplexe Aggregations-Query analysiert die Popularität von Menüartikeln
     * basierend auf der Anzahl der Bestellungen. Sie wird für Geschäftsanalysen
     * und Menüoptimierung verwendet.
     * <p>
     * Technische Details:
     * - Gruppierung nach Artikelname
     * - Zählung der Bestellungen pro Artikel
     * - Absteigende Sortierung nach Popularität
     * - Limitierung auf Top-N-Ergebnisse
     *
     * @param limit Maximale Anzahl der zurückgegebenen Artikel
     * @return Liste mit Maps containing 'name' und 'count' für jeden beliebten Artikel
     */
    @Query("SELECT oh.menuItemName as name, COUNT(oh) as count " +
            "FROM OrderHistory oh GROUP BY oh.menuItemName ORDER BY count DESC LIMIT :limit")
    List<Map<String, Object>> findMostOrderedItems(@Param("limit") int limit);

    /**
     * Berechnet den Gesamtumsatz für einen bestimmten Zeitraum.
     * <p>
     * Diese Aggregations-Query summiert alle Bestellwerte in einem Zeitraum
     * und wird für Finanzberichte und Umsatzanalysen verwendet.
     * <p>
     * Verwendung:
     * - Monatliche und jährliche Umsatzberichte
     * - Finanzplanung und Budgetierung
     * - Geschäftsperformance-Analysen
     * - Steuerreportings und Buchhaltung
     *
     * @param start Startzeit des Abrechnungszeitraums (inklusive)
     * @param end   Endzeit des Abrechnungszeitraums (inklusive)
     * @return Gesamtumsatz als double-Wert
     */
    @Query("SELECT SUM(oh.price) FROM OrderHistory oh WHERE oh.orderTime BETWEEN :start AND :end")
    double sumOrderPricesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Analysiert tägliche Verkaufszahlen für einen bestimmten Zeitraum.
     * <p>
     * Diese komplexe Query gruppiert Bestellungen nach Datum und summiert
     * die täglichen Umsätze. Sie wird für Trendanalysen und operative
     * Planungen verwendet.
     * <p>
     * Technische Details:
     * - Verwendung der DATE-Funktion für Tagesgruppierung
     * - Summierung der Preise pro Tag
     * - Chronologische Sortierung der Ergebnisse
     *
     * @param start Startdatum der Analyse (inklusive)
     * @param end   Enddatum der Analyse (inklusive)
     * @return Liste mit Maps containing 'date' und 'total' für jeden Tag
     */
    @Query("SELECT FUNCTION('DATE', oh.orderTime) as date, SUM(oh.price) as total " +
            "FROM OrderHistory oh WHERE oh.orderTime BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('DATE', oh.orderTime) ORDER BY date")
    List<Map<String, Object>> findDailySalesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Ermittelt die Schüler mit den meisten Bestellungen.
     * <p>
     * Diese Query analysiert das Bestellverhalten von Schülern und identifiziert
     * die aktivsten Nutzer des Cafeteria-Systems. Sie wird für Kundenanalysen
     * und Marketingzwecke verwendet.
     * <p>
     * Verwendung:
     * - Kundenloyalitätsprogramme
     * - Zielgruppen-Marketing
     * - Verhaltensanalysen
     * - Prämien- und Belohnungssysteme
     *
     * @param limit Maximale Anzahl der zurückgegebenen Schüler
     * @return Liste mit Maps containing 'name' und 'orderCount' für die aktivsten Schüler
     */
    @Query("SELECT s.name as name, COUNT(oh) as orderCount " +
            "FROM OrderHistory oh JOIN oh.student s " +
            "GROUP BY s.id ORDER BY orderCount DESC LIMIT :limit")
    List<Map<String, Object>> findStudentsWithMostOrders(@Param("limit") int limit);
}