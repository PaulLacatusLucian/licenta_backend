package com.cafeteria.cafeteria_plugin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT-Authentication-Filter für das Schulverwaltungssystem.
 *
 * Diese Klasse erweitert OncePerRequestFilter und wird bei jeder HTTP-Anfrage
 * ausgeführt, um JWT-Token zu validieren und den Sicherheitskontext zu setzen.
 * Der Filter ist ein zentraler Bestandteil der JWT-basierten Authentifizierung.
 *
 * Funktionsweise:
 * 1. Extraktion des JWT-Tokens aus dem Authorization-Header
 * 2. Validierung des Tokens und Extraktion des Benutzernamens
 * 3. Laden der Benutzerdetails aus der Datenbank
 * 4. Setzen des Authentication-Objekts im SecurityContext
 * 5. Weiterleitung der Anfrage an nachgelagerte Filter
 *
 * Besonderheiten:
 * - Bypass für öffentliche Endpunkte (Login, H2-Console)
 * - Detailliertes Logging für Debugging
 * - Sichere Token-Validierung
 * - Integration mit Spring Security Authentication
 *
 * @author Paul Lacatus
 * @version 1.0
 * @see JwtUtil
 * @see UserDetailsService
 * @see OncePerRequestFilter
 * @since 2025-01-01
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    /**
     * JWT-Utility für Token-Verarbeitung und -Validierung.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * UserDetailsService für das Laden von Benutzerinformationen aus der Datenbank.
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Filtert eingehende HTTP-Anfragen und führt JWT-Authentifizierung durch.
     *
     * Diese Methode wird bei jeder HTTP-Anfrage ausgeführt und implementiert
     * die vollständige JWT-Authentifizierungslogik. Sie überprüft das Vorhandensein
     * eines gültigen JWT-Tokens und setzt entsprechend den Sicherheitskontext.
     *
     * Ablauf:
     * 1. Logging der eingehenden Anfrage für Debugging
     * 2. Überprüfung auf öffentliche Endpunkte (Bypass)
     * 3. Extraktion und Validierung des JWT-Tokens
     * 4. Laden der Benutzerdetails
     * 5. Setzen des Authentication-Objekts
     * 6. Weiterleitung an nachgelagerte Filter
     *
     * @param request HTTP-Anfrage mit potentiellem JWT-Token
     * @param response HTTP-Antwort
     * @param chain Filter-Chain für Weiterleitung
     * @throws ServletException Falls ein Servlet-Fehler auftritt
     * @throws IOException Falls ein I/O-Fehler auftritt
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        // Detailliertes Logging für Debugging
        System.out.println("📌 Eingehende Anfrage: " + requestURI);
        System.out.println("🔍 Authorization Header: " + authHeader);

        // Bypass für öffentliche Endpunkte
        if (requestURI.equals("/auth/login") ||
                requestURI.equals("/auth/register-admin") ||
                requestURI.startsWith("/h2-console/")) {
            chain.doFilter(request, response);
            return;
        }

        // JWT-Token-Validierung für alle anderen Endpunkte
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // "Bearer " entfernen
            String username = jwtUtil.extractUsername(token);

            // Authentifizierung nur setzen, wenn noch keine vorhanden ist
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Token-Validierung gegen Benutzername
                if (jwtUtil.isTokenValid(token, userDetails.getUsername())) {
                    // Authentication-Objekt erstellen und konfigurieren
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Request-Details hinzufügen
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // SecurityContext setzen
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    System.out.println("✅ Benutzer erfolgreich authentifiziert: " + username);
                } else {
                    System.out.println("❌ Ungültiges Token für Benutzer: " + username);
                }
            }
        } else {
            System.out.println("⚠️ Kein gültiger Authorization Header gefunden");
        }

        // Anfrage an nachgelagerte Filter weiterleiten
        chain.doFilter(request, response);
    }
}