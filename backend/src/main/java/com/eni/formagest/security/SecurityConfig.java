package com.eni.formagest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> {
            auth
                    .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/logout").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/sectors/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/tracks/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/courses/**").permitAll()
                    .requestMatchers("/api/cohorts/**").hasRole("ADMINISTRATIVE_MANAGER")
                    .anyRequest().authenticated();
        });

        http.exceptionHandling(ex -> ex.authenticationEntryPoint(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
        ));

        // Désactivé volontairement malgré le token en cookie (donc envoyé automatiquement
        // par le navigateur) : l'API n'accepte que du JSON, qu'un <form> HTML classique ne
        // peut pas produire sans JavaScript (donc bloqué par CORS) ; et le cookie est posé en
        // SameSite=Lax (cf. JwtService.generateJwtCookie), qui empêche déjà son envoi lors
        // d'une requête initiée depuis un autre site.
        http.csrf(csrf -> csrf.disable());

        http.authenticationProvider(authenticationProvider);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }
}
