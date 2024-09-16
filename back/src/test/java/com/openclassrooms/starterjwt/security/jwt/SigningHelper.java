package com.openclassrooms.starterjwt.security.jwt;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Helper class for token testing
 */
public class SigningHelper {
    /**
     * Helper to create signed test token
     *
     * @param email
     * @param jwtExpirationMs
     * @param jwtSecret
     * @return
     */
    public static String sign(String email, int jwtExpirationMs, String jwtSecret) {
        return Jwts.builder()
                .setSubject((email))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Helper to create unsigned test token
     *
     * @param email
     * @param jwtExpirationMs
     * @param jwtSecret
     * @return
     */
    public static String unsigned(String email, int jwtExpirationMs, String jwtSecret) {
        return Jwts.builder()
                .setSubject((email))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .compact();
    }
}
