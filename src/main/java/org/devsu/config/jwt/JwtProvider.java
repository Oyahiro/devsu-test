package org.devsu.config.jwt;

import io.jsonwebtoken.*;
import org.devsu.dto.PrimaryUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Genera el token
 */

@Component
public class JwtProvider {

    private final Logger LOG = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    public String generateToken(Authentication authentication) {
        Date now = new Date();
        PrimaryUser primaryUser = (PrimaryUser) authentication.getPrincipal();
        return Jwts.builder().setSubject(primaryUser.getUsername())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + (expiration * 1000L)))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            LOG.error(String.format("Malformed token: %s", token));
        } catch (UnsupportedJwtException e) {
            LOG.error(String.format("Unsupported token: %s", token));
        } catch (ExpiredJwtException e) {
            LOG.error(String.format("Expired token: %s", token));
        } catch (IllegalArgumentException e) {
            LOG.error(String.format("Empty token: %s", token));
        } catch (SignatureException e) {
            LOG.error(String.format("Signature exception for token: %s", token));
        }
        return false;
    }

}
