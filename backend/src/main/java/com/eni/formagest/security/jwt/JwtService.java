package com.eni.formagest.security.jwt;

import com.eni.formagest.bo.users.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    // Déclarer une clef de sécurité, en utilisant
    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @Value("${app.jwt.expiration}")
    private Duration EXPIRATION_TIME;

    @Value("${app.jwt.cookie-name}")
    private String COOKIE_NAME;

    @Value("${app.jwt.cookie-secure}")
    private boolean COOKIE_SECURE;

    //Signature transmise pour la création du jeton.
    //Et chiffrer/déchiffrer les données du jeton
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extraire « claims » du jeton
    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
    }

    // Extraire 1 « claims » du jeton
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extraire l'email du jeton
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Générer le jeton JWT

    public String generateToken(Map<String, Object> extraClaims, String username) {
        return Jwts.builder().claims(extraClaims).subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME.toMillis()))
                .signWith(getSignInKey(), Jwts.SIG.HS256).compact();
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        return generateToken(claims, user.getUsername());
    }

    public ResponseCookie generateJwtCookie(User user) {
        String jwt = generateToken(user);
        return ResponseCookie.from(COOKIE_NAME, jwt)
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .path("/api")
                .maxAge(EXPIRATION_TIME)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie generateCleanJwtCookie() {
        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .path("/api")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

    // Validation du jeton
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
