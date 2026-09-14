package com.eni.formagest.controllers;

import com.eni.formagest.bo.users.User;
import com.eni.formagest.dto.auth.LoginRequest;
import com.eni.formagest.dto.auth.LoginResponse;
import com.eni.formagest.dto.users.UserDto;
import com.eni.formagest.security.jwt.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    // Pas de route /logout : le token JWT est sans état côté serveur, la déconnexion
    // consiste à l'effacer côté navigateur (sessionStorage).
    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authenticationService.getCurrentUser(user.getUsername()));
    }
}
