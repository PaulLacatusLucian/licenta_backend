package com.cafeteria.cafeteria_plugin.security;

import com.cafeteria.cafeteria_plugin.models.User.UserType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "MySuperSecretKeyForJwtMySuperSecretKeyForJwt"; // Min 32 caractere
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 oră

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(String username, UserType userType) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userType", userType.name()) // ✅ Adăugăm `UserType` în token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public UserType extractUserType(String token) {
        String userTypeName = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().get("userType", String.class);
        return UserType.valueOf(userTypeName); // ✅ Convertim String în `UserType`
    }

    public boolean isTokenValid(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}
