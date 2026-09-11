package com.eni.formagest.bo.users;

import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.dal.users.AdministrativeManagerRepository;
import com.eni.formagest.dal.users.AdministratorRepository;
import com.eni.formagest.dal.training.SectorRepository;
import com.eni.formagest.dal.users.StudentRepository;
import com.eni.formagest.dal.users.TeacherRepository;
import com.eni.formagest.dal.users.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
@Slf4j
class TestHeritageUser {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AdministrativeManagerRepository administrativeManagerRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

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
    void test_findAll() {
        List<User> users = userRepository.findAll();

        log.info(users.toString());

        Assertions.assertThat(users).hasSize(4);
    }

    @Test
    void test_findAllStudent() {
        List<Student> students = studentRepository.findAll();

        Assertions.assertThat(students).hasSize(1);
        Assertions.assertThat(students.get(0).getBirthDate()).isEqualTo(LocalDate.of(2000, 4, 12));
    }

    @Test
    void test_findAllTeacher() {
        List<Teacher> teachers = teacherRepository.findAll();

        Assertions.assertThat(teachers).hasSize(1);
        Assertions.assertThat(teachers.get(0).getSector().getName()).isEqualTo("Développement");
    }

    @Test
    void test_findAllAdministrativeManager() {
        Assertions.assertThat(administrativeManagerRepository.findAll()).hasSize(1);
    }

    @Test
    void test_findAllAdministrator() {
        Assertions.assertThat(administratorRepository.findAll()).hasSize(1);
    }
}
