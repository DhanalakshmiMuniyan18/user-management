package com.usermanagement.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Date;
import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private static final String SECRET_KEY = "your-secret-key-your-secret-key-your-secret-key-your-secret-key";
    private static final long ACCESS_TOKEN_VALIDITY = 3600000; // 1 hour
    private static final long REFRESH_TOKEN_VALIDITY = 86400000; // 24 hours
    private static final long TOKEN_BUFFER = 5000; // 5 seconds buffer
    private Key key;

    @BeforeEach
    void setUp() {
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        jwtTokenProvider = new JwtTokenProvider(key);
        jwtTokenProvider.setAccessTokenValidity(ACCESS_TOKEN_VALIDITY / 1000); // convert ms to seconds
        jwtTokenProvider.setRefreshTokenValidity(REFRESH_TOKEN_VALIDITY / 1000); // convert ms to seconds
    }

    @Test
    void generateAccessToken_ShouldCreateValidToken() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // Act
        String token = jwtTokenProvider.generateAccessToken(authentication);

        // Assert
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("testuser", jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    void generateRefreshToken_ShouldCreateValidToken() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // Act
        String token = jwtTokenProvider.generateRefreshToken(authentication);

        // Assert
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("testuser", jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    void getAuthentication_ShouldReturnCorrectAuthentication() {
        // Arrange
        String token = Jwts.builder()
                .setSubject("testuser")
                .claim("auth", "ROLE_USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY + TOKEN_BUFFER))
                .signWith(key)
                .compact();

        // Act
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        // Assert
        assertNotNull(authentication);
        assertEquals("testuser", authentication.getName());
        assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY + TOKEN_BUFFER))
                .signWith(key)
                .compact();

        // Act & Assert
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        // Arrange
        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - ACCESS_TOKEN_VALIDITY - 1000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        // Act & Assert
        assertFalse(jwtTokenProvider.validateToken(token));
    }

    @Test
    void getUsernameFromToken_ShouldReturnCorrectUsername() {
        // Arrange
        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY + TOKEN_BUFFER))
                .signWith(key)
                .compact();

        // Act
        String username = jwtTokenProvider.getUsernameFromToken(token);

        // Assert
        assertEquals("testuser", username);
    }
} 