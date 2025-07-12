package com.planit.planit.auth.jwt;

import com.planit.planit.member.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final long refreshTokenExpirationMs;

    @Autowired
    public JwtProvider(JwtProperties properties) {
        this.secretKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
        this.expirationMs = properties.getExpirationMs();
        this.refreshTokenExpirationMs = properties.getRefreshTokenExpirationMs();
    }

    public String createAccessToken(Long id, String email, String name, Role role) {
        return createToken(id, email, name, role, expirationMs);
    }

    public String createRefreshToken(Long id, String email, String name, Role role) {
        return createToken(id, email, name, role, refreshTokenExpirationMs);
    }

    private String createToken(Long id, String email, String name, Role role, long expirationMs) {
        final Date now = new Date();
        final Date expiry = new Date(now.getTime() + expirationMs);

        log.info("JWT_:PROV:TIME:::createdAt({}),expiredAt({})", now, expiry);

        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("email", email)
                .claim("memberName", name)
                .claim("role", role.toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String resolveHeaderToken(String headerValue, String prefix) {
        if (headerValue == null || !headerValue.startsWith(prefix)) {
            return null;
        }
        return headerValue.substring(prefix.length()).trim();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("JWT_:PROV:ERR_:::Invalid JWT Token. error({})", e.getMessage());
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

    public UserPrincipal getUserPrincipal(String token) {
        final Claims claims = getClaims(token);
        return new UserPrincipal(
                Long.valueOf(claims.getSubject()),
                claims.get("email", String.class),
                claims.get("memberName", String.class),
                Role.valueOf(claims.get("role", String.class))
        );
    }

    public long getRemainingValidity(String token) {
        final Date expiration = getClaims(token).getExpiration();
        long now = System.currentTimeMillis();
        long diff = (expiration.getTime() - now) / 1000; // 초 단위
        return Math.max(diff, 0);
    }

    // 토큰 만료 여부 반환
    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = getClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true; // 파싱 불가 시 만료로 간주
        }
    }

    public boolean isRefreshTokenTampered(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token); // 만료되었든 말든 시그니처 검증만 통과하면 됨
            return false;
        } catch (ExpiredJwtException e) {
            // 만료는 허용
            return false;
        } catch (JwtException e) {
            // 위조됨, 손상됨 등
            return true;
        }
    }
}

