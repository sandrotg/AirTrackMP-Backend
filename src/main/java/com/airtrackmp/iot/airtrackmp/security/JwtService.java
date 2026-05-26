package com.airtrackmp.iot.airtrackmp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import com.airtrackmp.iot.airtrackmp.entity.User;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private static final long DEFAULT_TOKEN_TTL_MS = 86_400_000L;
    private static final String NODE_ROLE = "NODE";

    public String generateToken(User user) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date());

        if (!NODE_ROLE.equals(user.getRole())) {
            builder.setExpiration(
                    new Date(System.currentTimeMillis() + DEFAULT_TOKEN_TTL_MS)
            );
        }

        return builder.signWith(getKey(), SignatureAlgorithm.HS256).compact();
    }

    public String extractUsername(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(
            String token,
            UserDetails user
    ) {

        String username = extractUsername(token);

        return username.equals(user.getUsername());
    }
}