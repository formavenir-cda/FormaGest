package com.eni.formagest.controllers.training;

import com.eni.formagest.bo.training.Course;
import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.bo.training.TrackCourse;
import com.eni.formagest.bo.users.Student;
import com.eni.formagest.bo.users.Teacher;
import com.eni.formagest.bo.users.UserRole;
import com.eni.formagest.dal.training.CohortRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CohortControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CohortRepository cohortRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createReturnsCreatedCohortWithOrderedDatedCourses() throws Exception {
        Track track = saveTrackWithCourses(
                "CDA test promotion",
                "Java",
                "Angular",
                "Spring"
        );

        mockMvc.perform(post("/api/cohorts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "CDA 2026",
                                  "startDate": "2026-09-01",
                                  "trackId": %d
                                }
                                """.formatted(track.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("CDA 2026"))
                .andExpect(jsonPath("$.startDate").value("2026-09-01"))
                .andExpect(jsonPath("$.endDate").value("2026-09-21"))
                .andExpect(jsonPath("$.status").value("UPCOMING"))
                .andExpect(jsonPath("$.trackId").value(track.getId()))
                .andExpect(jsonPath("$.scheduledCourses.length()").value(3))
                .andExpect(jsonPath("$.scheduledCourses[0].courseId").isNumber())
                .andExpect(jsonPath("$.scheduledCourses[0].startDate").value("2026-09-01"))
                .andExpect(jsonPath("$.scheduledCourses[0].endDate").value("2026-09-07"))
                .andExpect(jsonPath("$.scheduledCourses[1].startDate").value("2026-09-08"))
                .andExpect(jsonPath("$.scheduledCourses[1].endDate").value("2026-09-14"))
                .andExpect(jsonPath("$.scheduledCourses[2].startDate").value("2026-09-15"))
                .andExpect(jsonPath("$.scheduledCourses[2].endDate").value("2026-09-21"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createRejectsTrackWithoutCourses() throws Exception {
        Sector sector = Sector.builder()
                .name("Secteur cursus vide")
                .build();
        Track track = Track.builder()
                .name("Cursus vide")
                .sector(sector)
                .build();

        entityManager.persist(sector);
        entityManager.persist(track);
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(post("/api/cohorts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "CDA cursus vide",
                                  "startDate": "2026-09-01",
                                  "trackId": %d
                                }
                                """.formatted(track.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "Le cursus doit contenir au moins un cours."
                ));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createRejectsMissingTrack() throws Exception {
        mockMvc.perform(post("/api/cohorts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "CDA cursus inconnu",
                                  "startDate": "2026-09-01",
                                  "trackId": 42
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Ce cursus n'existe pas."));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createRejectsDuplicateName() throws Exception {
        Track track = saveTrackWithCourses("CDA doublon", "Java");

        cohortRepository.save(com.eni.formagest.bo.training.Cohort.builder()
                .name("CDA doublon 2026")
                .startDate(java.time.LocalDate.of(2026, 9, 1))
                .endDate(java.time.LocalDate.of(2027, 6, 30))
                .status(com.eni.formagest.bo.training.CohortStatus.UPCOMING)
                .track(track)
                .build());

        mockMvc.perform(post("/api/cohorts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "CDA doublon 2026",
                                  "startDate": "2026-09-01",
                                  "trackId": %d
                                }
                                """.formatted(track.getId())))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        "Une promotion portant ce nom existe déjà."
                ));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createAssignsTeachersToScheduledCourses() throws Exception {
        Track track = saveTrackWithCourses(
                "CDA affectation formateur",
                "Java"
        );
        Course course =
                entityManager.createQuery(
                                "select tc.course from TrackCourse tc where tc.track.id = :trackId",
                                Course.class
                        )
                        .setParameter("trackId", track.getId())
                        .getSingleResult();
        Teacher teacher = saveTeacher("CDA affectation formateur");

        mockMvc.perform(post("/api/cohorts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "CDA affectation formateur 2026",
                                  "startDate": "2026-09-01",
                                  "trackId": %d,
                                  "teacherAssignments": [
                                    {"courseId": %d, "teacherId": %d}
                                  ]
                                }
                                """.formatted(track.getId(), course.getId(), teacher.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.scheduledCourses[0].teacherId").value(teacher.getId()))
                .andExpect(jsonPath("$.scheduledCourses[0].teacherName").value(
                        teacher.getFirstName() + " " + teacher.getLastName()
                ));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createRejectsNonTeacherAsAssignedTeacher() throws Exception {
        Track track = saveTrackWithCourses(
                "CDA formateur invalide",
                "Java"
        );
        Course course =
                entityManager.createQuery(
                                "select tc.course from TrackCourse tc where tc.track.id = :trackId",
                                Course.class
                        )
                        .setParameter("trackId", track.getId())
                        .getSingleResult();
        Student student = saveStudent("CDA formateur invalide");

        mockMvc.perform(post("/api/cohorts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "CDA formateur invalide 2026",
                                  "startDate": "2026-09-01",
                                  "trackId": %d,
                                  "teacherAssignments": [
                                    {"courseId": %d, "teacherId": %d}
                                  ]
                                }
                                """.formatted(track.getId(), course.getId(), student.getId())))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Le formateur sélectionné n'existe pas."));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void createReturnsForbiddenForUnauthorizedRole() throws Exception {
        mockMvc.perform(post("/api/cohorts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "CDA interdit",
                                  "startDate": "2026-09-01",
                                  "trackId": 1
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    private Track saveTrackWithCourses(String trackName, String... courseNames) {
        Sector sector = Sector.builder()
                .name("Secteur " + trackName)
                .build();
        Track track = Track.builder()
                .name("Cursus " + trackName)
                .sector(sector)
                .build();

        entityManager.persist(sector);
        entityManager.persist(track);

        for (int index = 0; index < courseNames.length; index++) {
            Course course = Course.builder()
                    .name(courseNames[index] + " " + trackName)
                    .durationInDays(5)
                    .build();

            entityManager.persist(course);
            entityManager.persist(TrackCourse.builder()
                    .track(track)
                    .course(course)
                    .position(index + 1)
                    .build());
        }

        entityManager.flush();
        entityManager.clear();

        return track;
    }

    private Teacher saveTeacher(String name) {
        Sector sector = Sector.builder()
                .name("Secteur formateur " + name)
                .build();
        entityManager.persist(sector);

        Teacher teacher = Teacher.builder()
                .email("formateur." + name.replace(" ", "-").toLowerCase() + "@formagest.test")
                .lastName("Formateur")
                .firstName(name)
                .password("password")
                .active(true)
                .role(UserRole.TEACHER)
                .sector(sector)
                .build();

        entityManager.persist(teacher);
        entityManager.flush();
        entityManager.clear();

        return teacher;
    }

    private Student saveStudent(String name) {
        Student student = Student.builder()
                .email("eleve." + name.replace(" ", "-").toLowerCase() + "@formagest.test")
                .lastName("Élève")
                .firstName(name)
                .password("password")
                .active(true)
                .role(UserRole.STUDENT)
                .birthDate(java.time.LocalDate.of(2000, 1, 1))
                .build();

        entityManager.persist(student);
        entityManager.flush();
        entityManager.clear();

        return student;
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void findAllReturnsOkWithCohorts() throws Exception {
        Track track = saveTrackWithCourses("CDA liste promotions", "Java");

        cohortRepository.save(com.eni.formagest.bo.training.Cohort.builder()
                .name("CDA 2026")
                .startDate(java.time.LocalDate.of(2026, 9, 1))
                .endDate(java.time.LocalDate.of(2027, 6, 30))
                .status(com.eni.formagest.bo.training.CohortStatus.UPCOMING)
                .track(track)
                .build());

        mockMvc.perform(get("/api/cohorts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("CDA 2026"))
                .andExpect(jsonPath("$[0].trackId").value(track.getId()));
    }
}
