package com.planit.planit.auth.jwt;

import com.planit.planit.member.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

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

        log.info("JWT_:PROV:CRTE:::토큰을 생성합니다. createdAt({}),expiredAt({})", now, expiry);

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
            Assert.notNull(token, "Token must not be null");
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("JWT_:PROV:ERR_:::만료된 토큰입니다. msg({})", e.getMessage());
            throw new JwtException(e.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            log.info("JWT_:PROV:ERR_:::위변조가 발생한 토큰입니다. msg({})", e.getMessage());
            throw new JwtException(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JWT_:PROV:ERR_:::잘못된 형식의 토큰입니다. error({})", e.getMessage());
            throw new JwtException(e.getMessage());
        } catch (Exception e) {
            log.info("JWT_:PROV:ERR_:::토큰 파싱 과정에서 문제가 발생했습니다. error({})", e.getMessage());
            throw new JwtException(e.getMessage());
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
        } catch (ExpiredJwtException e) {
            log.info("JWT_:PROV:ERR_:::만료된 토큰입니다. msg({})", e.getMessage());
            return true;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            log.info("JWT_:PROV:ERR_:::위변조가 발생한 토큰입니다. msg({})", e.getMessage());
            return true;
        } catch (IllegalArgumentException e) {
            log.info("JWT_:PROV:ERR_:::잘못된 형식의 토큰입니다. error({})", e.getMessage());
            return true;
        } catch (Exception e) {
            log.info("JWT_:PROV:ERR_:::토큰 파싱 과정에서 문제가 발생했습니다. error({})", e.getMessage());
            return true;
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
            return false;                   // 만료된 토큰 허용
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            log.info("JWT_:PROV:ERR_:::위변조가 발생한 토큰입니다. msg({})", e.getMessage());
            return true;
        } catch (IllegalArgumentException e) {
            log.info("JWT_:PROV:ERR_:::잘못된 형식의 토큰입니다. error({})", e.getMessage());
            return true;
        } catch (Exception e) {
            log.info("JWT_:PROV:ERR_:::토큰 파싱 과정에서 문제가 발생했습니다. error({})", e.getMessage());
            return true;
        }
    }
}

