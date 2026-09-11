package com.eni.formagest.dto.users;

import com.eni.formagest.bo.users.UserRole;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String email;
    private String lastName;
    private String firstName;
    private boolean active;
    private UserRole role;

    // Renseigné uniquement pour un utilisateur de rôle STUDENT
    private LocalDate birthDate;

    // Renseigné uniquement pour un utilisateur de rôle TEACHER
    private Long sectorId;
}
