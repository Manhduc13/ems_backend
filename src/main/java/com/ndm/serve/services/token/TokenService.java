package com.ndm.serve.services.token;

import org.springframework.security.core.Authentication;

public interface TokenService {
    String generateToken(Authentication authentication);

    Authentication getAuthentication(String token);

    boolean validateToken(String token);
}
