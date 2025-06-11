package com.cafeteria.cafeteria_plugin.security;

import com.cafeteria.cafeteria_plugin.models.User.UserType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility-Klasse für die Verwaltung von JSON Web Tokens (JWT).
 * @author Paul Lacatus
 * @version 1.0
 * @see UserType
 * @since 2025-03-12
 */
@Component
public class JwtUtil {

    /**
     * Geheimer Schlüssel für JWT-Signierung.
     * Muss mindestens 32 Zeichen lang sein für HMAC SHA-256.
     *
     * @deprecated In Produktionsumgebungen sollte dieser Schlüssel
     * aus einer externen Konfiguration oder Umgebungsvariable geladen werden.
     */
    private static final String SECRET_KEY = "MySuperSecretKeyForJwtMySuperSecretKeyForJwt";

    /**
     * Token-Gültigkeitsdauer in Millisekunden (1 Stunde).
     * Nach Ablauf dieser Zeit müssen Benutzer sich erneut anmelden.
     */
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 Stunde

    /**
     * Sicherer Verschlüsselungsschlüssel, generiert aus dem geheimen Schlüssel.
     * Wird für alle kryptographischen Operationen verwendet.
     */
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * Generiert ein neues JWT-Token für einen authentifizierten Benutzer.
     *
     * Das Token enthält den Benutzernamen als Subject und den Benutzertyp
     * als Custom Claim. Diese Informationen ermöglichen es dem System,
     * den Benutzer zu identifizieren und seine Berechtigungen zu bestimmen,
     * ohne die Datenbank bei jeder Anfrage zu konsultieren.
     *
     * @param username Eindeutiger Benutzername des authentifizierten Benutzers
     * @param userType Typ des Benutzers (bestimmt Berechtigungen und Rollen)
     * @return Signiertes JWT-Token als String
     */
    public String generateToken(String username, UserType userType) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userType", userType.name()) // Benutzertyp in Token einbetten
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrahiert den Benutzernamen aus einem JWT-Token.
     *
     * Diese Methode parst das Token und gibt den Subject-Claim zurück,
     * der den Benutzernamen enthält. Der Benutzername wird für die
     * Identifikation des Benutzers in nachgelagerten Services verwendet.
     *
     * @param token Das zu parsende JWT-Token
     * @return Benutzername aus dem Token
     * @throws JwtException Falls das Token ungültig ist oder nicht geparst werden kann
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Extrahiert den Benutzertyp aus einem JWT-Token.
     *
     * Diese Methode parst das Token und gibt den Custom Claim 'userType' zurück,
     * der für die rollenbasierte Zugriffskontrolle verwendet wird.
     *
     * @param token Das zu parsende JWT-Token
     * @return Benutzertyp aus dem Token
     * @throws JwtException Falls das Token ungültig ist
     * @throws IllegalArgumentException Falls der Benutzertyp ungültig ist
     */
    public UserType extractUserType(String token) {
        String userTypeName = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("userType", String.class);
        return UserType.valueOf(userTypeName); // String zu UserType konvertieren
    }

    /**
     * Validiert ein JWT-Token gegen einen gegebenen Benutzernamen.
     *
     * Diese Methode überprüft zwei kritische Aspekte:
     * 1. Übereinstimmung des Benutzernamens im Token mit dem erwarteten Benutzernamen
     * 2. Gültigkeit des Tokens (nicht abgelaufen)
     *
     * @param token Das zu validierende JWT-Token
     * @param username Der erwartete Benutzername
     * @return true wenn das Token gültig ist, false andernfalls
     */
    public boolean isTokenValid(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    /**
     * Überprüft, ob ein JWT-Token abgelaufen ist.
     *
     * Diese private Hilfsmethode vergleicht die Ablaufzeit des Tokens
     * mit der aktuellen Zeit und bestimmt, ob das Token noch gültig ist.
     *
     * @param token Das zu überprüfende JWT-Token
     * @return true wenn das Token abgelaufen ist, false wenn es noch gültig ist
     * @throws JwtException Falls das Token nicht geparst werden kann
     */
    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}