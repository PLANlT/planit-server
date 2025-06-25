package com.planit.planit.config.jwt;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final long refreshTokenExpirationMs;
    private final String tokenPrefix;
    private final String issuer;
    private final List<String> audiences;

    @Autowired
    public JwtProvider(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        this.expirationMs = jwtProperties.getExpirationMs();
        this.refreshTokenExpirationMs = jwtProperties.getRefreshTokenExpirationMs();
        this.tokenPrefix = jwtProperties.getTokenPrefix();
        this.issuer = jwtProperties.getIssuer();
        this.audiences = jwtProperties.getAudiences();
    }

    public String createToken(Long id, String email, String name, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(id)) // sub: id
                .claim("email", email)
                .claim("memberName", name)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }

    public String getEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String getMemberName(String token) {return getClaims(token).get("memberName", String.class);}

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

