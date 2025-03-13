package com.ndm.serve.services.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TokenServiceImpl implements TokenService {
    @Value("${app.security.access-token-secret-key}")
    private String secretKey;

    @Value("${app.security.access-token-expired-in-second}")
    private Integer expireTime;

    @Override
    public String generateToken(Authentication authentication) {
        String roles = authentication.getAuthorities().stream().map(Object::toString).collect(Collectors.joining(",")); // Admin,User

        return generateAccessToken(authentication.getName(), roles);
    }

    private String generateAccessToken(String name, String roles) {
        LocalDateTime expiredAt = LocalDateTime.now().plusSeconds(expireTime); // Now + 3600s from

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)); // Decode secretKey from Base64 to
        // SecretKey

        Date expiration = Date.from(expiredAt.atZone(ZoneId.systemDefault()).toInstant()); // Convert LocalDateTime to Date

        // Generate JWT token
        return Jwts.builder()
                .subject(name)
                .claim("roles", roles)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    @Override
    public Authentication getAuthentication(String token) {
        if (token == null) {
            return null;
        }

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)); // Decode secretKey from Base64 to SecretKey

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String roles = claims.get("roles").toString();

            Set<GrantedAuthority> authorities = Set.of(roles.split(",")).stream()
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

            User principle = new User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(principle, token, authorities);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
