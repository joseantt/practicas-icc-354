package org.example.practica3.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.practica3.entities.Mockup;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // En producción, esta clave debería estar en un lugar seguro, como variables de entorno
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(Mockup mockup) {
        long expirationHours = mockup.getExpirationTimeInHours();
        Date expirationDate = new Date(System.currentTimeMillis() + (expirationHours * 60 * 60 * 1000));

        return Jwts.builder()
                .setSubject(mockup.getPath())
                .claim("mockupId", mockup.getId())
                .claim("method", mockup.getAccessMethod())
                .setExpiration(expirationDate)
                .signWith(KEY)
                .compact();
    }

    public boolean validateToken(String token, String path, String method) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject().equals(path) &&
                    claims.get("method", String.class).equals(method) &&
                    !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}