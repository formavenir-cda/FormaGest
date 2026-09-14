package com.eni.formagest.bo.users;

import com.eni.formagest.bo.training.CohortStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "password")
@SuperBuilder
@EqualsAndHashCode(of = "email")
@Entity
@Table(name = "APP_USER")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User implements UserDetails {

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

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

//    @Override
//    public boolean isEnabled() {
//        return active;
//    }
//
//    // Etat du compte utilisateur – compte non expiré ?
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//    // Etat du compte utilisateur – non verrouillé ?
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//    // Indique si les informations d’identification sont non expirées ?
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
}
