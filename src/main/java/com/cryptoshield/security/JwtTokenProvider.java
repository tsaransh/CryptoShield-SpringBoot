package com.cryptoshield.security;

import com.cryptoshield.Exception.ApiException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key jwtSecret;
    private final int jwtExpirationInMs;

    public JwtTokenProvider(@Value("${app.jwt-expiration-milliseconds}") int jwtExpirationInMs) {
        this.jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.jwtExpirationInMs = jwtExpirationInMs;
    }


    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch(SignatureException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid JWT signature");
        }
        catch(MalformedJwtException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        }
        catch(ExpiredJwtException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "expired JWT token");
        }
        catch(UnsupportedJwtException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "unsupported JWT token");
        }
        catch(IllegalArgumentException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "JWT claims string is empty");
        }
    }

}
