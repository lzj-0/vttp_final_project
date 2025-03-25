package com.recipes.FlavourFoundry.service;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

    private String secretKey = "";

    public JWTService() {

        // randomly generate a secretKey upon instantiation
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
            System.out.println(secretKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // generate jwt token
    public String generateToken(String email) throws InvalidKeyException, NoSuchAlgorithmException {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder().claims()
                            .add(claims)
                            .subject(email)
                            .issuedAt(new Date(System.currentTimeMillis()))
                            .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                            .and()
                            .signWith(getKey())
                            .compact();
    }

    public SecretKey getKey() throws NoSuchAlgorithmException {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractEmail(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException {
        String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException {
        System.out.println(extractExpiration(token).toString());
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) throws JwtException, IllegalArgumentException, NoSuchAlgorithmException {
        return extractClaim(token, Claims::getExpiration);
    }


}
