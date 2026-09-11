package com.eni.formagest.bo.users;

import com.eni.formagest.bo.training.CohortStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "password")
@SuperBuilder
@EqualsAndHashCode(of = "email")
@Entity
@Table(name = "APP_USER")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "LASTNAME", nullable = false)
    private String lastName;

    @Column(name = "FIRSTNAME", nullable = false)
    private String firstName;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active;

    @Column(name = "ROLE", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
