package com.ride.config;

import com.ride.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Date;

@Component
@Slf4j
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(user.getCpf())
                .claim("role", user.getRole().name())
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);

            if (isTokenExpired(claims)) {
                log.info("Token is expired.");
                return false;
            }
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logJwtException(e);
        }
        return false;
    }

    private Claims parseToken(String token) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        return jws.getPayload();
    }

    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration != null && expiration.before(new Date());
    }

    private void logJwtException(Exception e) {
        if (e instanceof SignatureException) {
            log.info("Invalid JWT signature.");
        } else if (e instanceof MalformedJwtException) {
            log.info("Invalid JWT token.");
        } else if (e instanceof ExpiredJwtException) {
            log.info("Expired JWT token.");
        } else if (e instanceof UnsupportedJwtException) {
            log.info("Unsupported JWT token.");
        } else if (e instanceof IllegalArgumentException) {
            log.info("JWT token compact of handler are invalid.");
        }
        log.trace("JWT validation exception trace: {}", e);
    }

    public String getCpfFromToken(String token) {
        return parseToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return parseToken(token).get("role", String.class);
    }

}
