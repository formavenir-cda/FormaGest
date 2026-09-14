package com.eni.formagest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class AppExceptionHandler {

    // Même message dans les deux cas pour ne pas révéler quels comptes existent.
    @ExceptionHandler(value = {BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<String> capturerAuthentificationInvalide(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiant ou mot de passe incorrect");
    }

    @ExceptionHandler(value = {DisabledException.class})
    public ResponseEntity<String> capturerCompteDesactive(DisabledException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Compte désactivé");
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<String> capturerException(MethodArgumentNotValidException e) {

        String message = e.getFieldErrors()
                .stream()
                .map(f -> f.getField() + " : " + f.getDefaultMessage())
                .collect(Collectors.joining(" - "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
