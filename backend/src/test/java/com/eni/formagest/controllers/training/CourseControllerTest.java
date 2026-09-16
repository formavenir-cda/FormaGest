package com.eni.formagest.controllers.training;

import com.eni.formagest.bo.training.Course;
import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.bo.training.TrackCourse;
import com.eni.formagest.dal.training.CourseRepository;
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

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void findAllReturnsOkWithCourses() throws Exception {
        Course course = courseRepository.save(Course.builder()
                .name("Java")
                .build());

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(course.getId()))
                .andExpect(jsonPath("$[0].name").value("Java"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void findAllReturnsForbiddenForUnauthorizedRole() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createReturnsCreatedWithCourse() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Java"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Java"));
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
    void createRejectsNameLongerThan255Characters() throws Exception {
        String body = "{\"name\":\"" + "a".repeat(256) + "\"}";

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
                .build());

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Java"}
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
                .build());

        mockMvc.perform(put("/api/courses/{id}", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Java"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(course.getId()))
                .andExpect(jsonPath("$.name").value("Java"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateRejectsBlankName() throws Exception {
        Course course = courseRepository.save(Course.builder()
                .name("Java")
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
                                {"name": "Java"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Ce cours n’existe pas."));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateReturnsConflictWithDuplicateMessage() throws Exception {
        Course course = courseRepository.save(Course.builder()
                .name("Java")
                .build());
        courseRepository.save(Course.builder()
                .name("Angular")
                .build());

        mockMvc.perform(put("/api/courses/{id}", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Angular"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        "Un cours portant ce nom existe déjà."
                ));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void deleteReturnsNoContent() throws Exception {
        Course course = courseRepository.saveAndFlush(Course.builder()
                .name("Java")
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
}
