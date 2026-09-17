package com.eni.formagest.controllers.training;

import com.eni.formagest.bo.training.Cohort;
import com.eni.formagest.bo.training.CohortStatus;
import com.eni.formagest.bo.training.Course;
import com.eni.formagest.bo.training.ScheduledCourse;
import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.bo.training.TrackCourse;
import com.eni.formagest.dal.training.CourseRepository;
import com.eni.formagest.dal.training.TrackCourseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TrackCourseRepository trackCourseRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void findAllReturnsOkWithCourses() throws Exception {
        Course course = courseRepository.save(Course.builder()
                .name("Java")
                .durationInDays(5)
                .build());

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(course.getId()))
                .andExpect(jsonPath("$[0].name").value("Java"))
                .andExpect(jsonPath("$[0].durationInDays").value(5));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void findAllReturnsOkForStudentRole() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createReturnsCreatedWithCourse() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Java", "durationInDays": 5}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Java"))
                .andExpect(jsonPath("$.durationInDays").value(5));
    }

    @ParameterizedTest
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    @ValueSource(strings = {
            "{}",
            "{\"name\": null}",
            "{\"name\": \"\"}",
            "{\"name\": \"   \"}"
    })
    void createRejectsMissingOrBlankName(String body) throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "Le nom du cours est obligatoire."
                )));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createRejectsDurationLessThanOne() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Java", "durationInDays": 0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "La durée doit être supérieure à 0."
                )));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createRejectsMissingDuration() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Java"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "La durée est obligatoire."
                )));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createRejectsNameLongerThan255Characters() throws Exception {
        String body = "{\"name\":\"" + "a".repeat(256) + "\",\"durationInDays\":5}";

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "Le nom ne doit pas dépasser 255 caractères."
                )));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createReturnsConflictWithDuplicateMessage() throws Exception {
        courseRepository.save(Course.builder()
                .name("Java")
                .durationInDays(5)
                .build());

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Java", "durationInDays": 5}
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        "Un cours portant ce nom existe déjà."
                ));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateReturnsUpdatedCourse() throws Exception {
        Course course = courseRepository.save(Course.builder()
                .name("Ancien nom")
                .durationInDays(5)
                .build());

        mockMvc.perform(put("/api/courses/{id}", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Java", "durationInDays": 7}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(course.getId()))
                .andExpect(jsonPath("$.name").value("Java"))
                .andExpect(jsonPath("$.durationInDays").value(7));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateRejectsBlankName() throws Exception {
        Course course = courseRepository.save(Course.builder()
                .name("Java")
                .durationInDays(5)
                .build());

        mockMvc.perform(put("/api/courses/{id}", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "   "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "Le nom du cours est obligatoire."
                )));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateReturnsNotFoundWithMessage() throws Exception {
        mockMvc.perform(put("/api/courses/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Java", "durationInDays": 5}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Ce cours n’existe pas."));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateReturnsConflictWithDuplicateMessage() throws Exception {
        Course course = courseRepository.save(Course.builder()
                .name("Java")
                .durationInDays(5)
                .build());
        courseRepository.save(Course.builder()
                .name("Angular")
                .durationInDays(5)
                .build());

        mockMvc.perform(put("/api/courses/{id}", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Angular", "durationInDays": 5}
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        "Un cours portant ce nom existe déjà."
                ));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateReturnsConflictWhenCourseIsUsedInCohort() throws Exception {
        Course course = saveScheduledCourse("Java cours utilise controller update");

        mockMvc.perform(put("/api/courses/{id}", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Java modifie", "durationInDays": 7}
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        "Impossible de modifier ce cours : il est utilisé dans une promotion."
                ));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateTracksReturnsCourseWithAssociations() throws Exception {
        Course course = courseRepository.save(Course.builder()
                .name("Java")
                .durationInDays(5)
                .build());
        Sector sector = Sector.builder()
                .name("Secteur update associations")
                .build();
        Track firstTrack = Track.builder()
                .name("Cursus update associations 1")
                .sector(sector)
                .build();
        Track secondTrack = Track.builder()
                .name("Cursus update associations 2")
                .sector(sector)
                .build();

        entityManager.persist(sector);
        entityManager.persist(firstTrack);
        entityManager.persist(secondTrack);
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(put("/api/courses/{id}/tracks", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [%d, %d]
                                """.formatted(firstTrack.getId(), secondTrack.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(course.getId()))
                .andExpect(jsonPath("$.associations.length()").value(2))
                .andExpect(jsonPath("$.associations[0].trackId").value(firstTrack.getId()))
                .andExpect(jsonPath("$.associations[0].position").value(1))
                .andExpect(jsonPath("$.associations[1].trackId").value(secondTrack.getId()))
                .andExpect(jsonPath("$.associations[1].position").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateTracksRemovesAssociationAndCompactsRemainingPositions() throws Exception {
        Sector sector = Sector.builder()
                .name("Secteur retrait association")
                .build();
        Track track = Track.builder()
                .name("Cursus retrait association")
                .sector(sector)
                .build();
        Course firstCourse = Course.builder()
                .name("Java retrait association")
                .durationInDays(5)
                .build();
        Course secondCourse = Course.builder()
                .name("Angular retrait association")
                .durationInDays(5)
                .build();

        entityManager.persist(sector);
        entityManager.persist(track);
        entityManager.persist(firstCourse);
        entityManager.persist(secondCourse);
        entityManager.persist(TrackCourse.builder()
                .track(track)
                .course(firstCourse)
                .position(1)
                .build());
        entityManager.persist(TrackCourse.builder()
                .track(track)
                .course(secondCourse)
                .position(2)
                .build());
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(put("/api/courses/{id}/tracks", firstCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(firstCourse.getId()))
                .andExpect(jsonPath("$.associations.length()").value(0));

        List<TrackCourse> remainingTrackCourses =
                trackCourseRepository.findByTrackIdOrderByPositionAsc(track.getId());

        assertEquals(1, remainingTrackCourses.size());
        assertEquals(secondCourse.getId(), remainingTrackCourses.getFirst().getCourse().getId());
        assertEquals(1, remainingTrackCourses.getFirst().getPosition());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void deleteReturnsNoContent() throws Exception {
        Course course = courseRepository.saveAndFlush(Course.builder()
                .name("Java")
                .durationInDays(5)
                .build());

        mockMvc.perform(delete("/api/courses/{id}", course.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertFalse(courseRepository.existsById(course.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void deleteReturnsNotFoundWithMessage() throws Exception {
        mockMvc.perform(delete("/api/courses/42"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Ce cours n’existe pas."));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void deleteReturnsConflictWhenCourseIsReferenced() throws Exception {
        Course course = courseRepository.saveAndFlush(Course.builder()
                .name("Java")
                .durationInDays(5)
                .build());
        Sector sector = Sector.builder()
                .name("Secteur test cours controller")
                .build();
        Track track = Track.builder()
                .name("Cursus test cours controller")
                .sector(sector)
                .build();

        entityManager.persist(sector);
        entityManager.persist(track);
        entityManager.persist(TrackCourse.builder()
                .track(track)
                .course(course)
                .position(1)
                .build());
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(delete("/api/courses/{id}", course.getId()))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        "Impossible de supprimer ce cours : "
                                + "il est encore utilisé par des données associées."
                ));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void deleteReturnsConflictWhenCourseIsUsedInCohort() throws Exception {
        Course course = saveScheduledCourse("Java cours utilise controller delete");

        mockMvc.perform(delete("/api/courses/{id}", course.getId()))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        "Impossible de modifier ce cours : il est utilisé dans une promotion."
                ));
    }

    private Course saveScheduledCourse(String courseName) {
        Course course = Course.builder()
                .name(courseName)
                .durationInDays(5)
                .build();
        Sector sector = Sector.builder()
                .name("Secteur " + courseName)
                .build();
        Track track = Track.builder()
                .name("Cursus " + courseName)
                .sector(sector)
                .build();
        Cohort cohort = Cohort.builder()
                .name("Promotion " + courseName)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 7))
                .status(CohortStatus.UPCOMING)
                .track(track)
                .build();

        entityManager.persist(sector);
        entityManager.persist(track);
        entityManager.persist(course);
        entityManager.persist(cohort);
        entityManager.persist(ScheduledCourse.builder()
                .cohort(cohort)
                .course(course)
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 7))
                .build());
        entityManager.flush();
        entityManager.clear();

        return course;
    }
}
