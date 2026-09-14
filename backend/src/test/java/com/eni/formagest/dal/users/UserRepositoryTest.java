package com.eni.formagest.dal.users;

import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.users.AdministrativeManager;
import com.eni.formagest.bo.users.Administrator;
import com.eni.formagest.bo.users.Student;
import com.eni.formagest.bo.users.Teacher;
import com.eni.formagest.bo.users.User;
import com.eni.formagest.bo.users.UserRole;
import com.eni.formagest.dal.training.SectorRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @BeforeEach
    void init() {
        Sector sector = sectorRepository.save(Sector.builder()
                .name("Développement")
                .build());

        userRepository.save(Student.builder()
                .email("eleve@test.fr")
                .lastName("Martin")
                .firstName("Lucas")
                .password("hash")
                .active(true)
                .role(UserRole.STUDENT)
                .birthDate(LocalDate.of(2000, 4, 12))
                .build());

        userRepository.save(Teacher.builder()
                .email("formateur@test.fr")
                .lastName("Bernard")
                .firstName("Claire")
                .password("hash")
                .active(true)
                .role(UserRole.TEACHER)
                .sector(sector)
                .build());

        userRepository.save(AdministrativeManager.builder()
                .email("referente@test.fr")
                .lastName("Dubois")
                .firstName("Sophie")
                .password("hash")
                .active(true)
                .role(UserRole.ADMINISTRATIVE_MANAGER)
                .build());

        userRepository.save(Administrator.builder()
                .email("admin@test.fr")
                .lastName("Petit")
                .firstName("Thomas")
                .password("hash")
                .active(true)
                .role(UserRole.ADMINISTRATOR)
                .build());
    }

    @Test
    void findAll_renvoieLes4UtilisateursAvecLeurTypeConcret() {
        List<User> users = userRepository.findAll();

        Assertions.assertThat(users).hasSize(4);
        Assertions.assertThat(users).hasAtLeastOneElementOfType(Student.class);
        Assertions.assertThat(users).hasAtLeastOneElementOfType(Teacher.class);
        Assertions.assertThat(users).hasAtLeastOneElementOfType(AdministrativeManager.class);
        Assertions.assertThat(users).hasAtLeastOneElementOfType(Administrator.class);
    }

    @Test
    void findByEmail_surUnTeacher_renvoieUnUserDeTypeTeacher() {
        User user = userRepository.findByEmail("formateur@test.fr").orElseThrow();

        Assertions.assertThat(user).isInstanceOf(Teacher.class);
        Assertions.assertThat(((Teacher) user).getSector().getName()).isEqualTo("Développement");
    }

    @Test
    void chaqueSousType_porteLeRoleAttendu() {
        Assertions.assertThat(userRepository.findByEmail("eleve@test.fr").orElseThrow().getRole())
                .isEqualTo(UserRole.STUDENT);
        Assertions.assertThat(userRepository.findByEmail("formateur@test.fr").orElseThrow().getRole())
                .isEqualTo(UserRole.TEACHER);
        Assertions.assertThat(userRepository.findByEmail("referente@test.fr").orElseThrow().getRole())
                .isEqualTo(UserRole.ADMINISTRATIVE_MANAGER);
        Assertions.assertThat(userRepository.findByEmail("admin@test.fr").orElseThrow().getRole())
                .isEqualTo(UserRole.ADMINISTRATOR);
    }
}
