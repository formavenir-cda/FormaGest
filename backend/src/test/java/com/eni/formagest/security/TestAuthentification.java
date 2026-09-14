package com.eni.formagest.security;

import com.eni.formagest.bo.users.Administrator;
import com.eni.formagest.bo.users.User;
import com.eni.formagest.bo.users.UserRole;
import com.eni.formagest.dal.users.UserRepository;
import com.eni.formagest.security.jwt.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import javax.crypto.SecretKey;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TestAuthentification {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @BeforeEach
    void init() {
        userRepository.save(Administrator.builder()
                .email("active@test.fr")
                .lastName("Petit")
                .firstName("Thomas")
                .password(passwordEncoder.encode("Formagest2026!"))
                .active(true)
                .role(UserRole.ADMINISTRATOR)
                .build()
        );
        userRepository.save(Administrator.builder()
                .email("inactive@test.fr")
                .lastName("Dupuis")
                .firstName("Alex")
                .password(passwordEncoder.encode("Formagest2026!"))
                .active(false)
                .role(UserRole.ADMINISTRATOR)
                .build()
        );
    }

    @Test
    void login_avecIdentifiantsValides_delivreTokenEtRole() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                              {"email":"active@test.fr","password":"Formagest2026!"}
                              """))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("formagest_token"))
                .andExpect(jsonPath("$.user.role").value("ADMINISTRATOR"));
    }

    @Test
    void login_avecMotDePasseErrone_estRefuseSansToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                              {"email":"active@test.fr","password":"mauvais-mot-de-passe"}
                              """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    void login_avecCompteDesactive_estRefuse() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                              {"email":"inactive@test.fr","password":"Formagest2026!"}
                              """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_sansToken_repond401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_avecTokenExpire_repond401() throws Exception {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        String tokenExpire = Jwts.builder()
                .subject("active@test.fr")
                .issuedAt(new Date(System.currentTimeMillis() - 10_000))
                .expiration(new Date(System.currentTimeMillis() - 5_000))
                .signWith(key, Jwts.SIG.HS256)
                .compact();

        mockMvc.perform(get("/api/auth/me").cookie(new jakarta.servlet.http.Cookie("formagest_token", tokenExpire)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_avecTokenValide_repondAvecLUtilisateurCourant() throws Exception {
        User user = userRepository.findByEmail("active@test.fr").orElseThrow();
        String token = jwtService.generateToken(user);

        mockMvc.perform(get("/api/auth/me").cookie(new jakarta.servlet.http.Cookie("formagest_token", token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("active@test.fr"));
    }
}
