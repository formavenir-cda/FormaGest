package com.eni.formagest.bll.training;

import com.eni.formagest.bo.training.Course;
import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.bo.training.TrackCourse;
import com.eni.formagest.dal.training.CourseRepository;
import com.eni.formagest.dto.training.CourseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void findAllReturnsCourses() {
        Course course = courseRepository.save(Course.builder()
                .name("Java")
                .build());

        List<CourseDto> result = courseService.findAll();

        assertEquals(1, result.size());
        assertEquals(course.getId(), result.getFirst().getId());
        assertEquals("Java", result.getFirst().getName());
    }

    @Test
    void findAllReturnsEmptyList() {
        List<CourseDto> result = courseService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void createTrimsNameAndIgnoresProvidedId() {
        CourseDto dto = CourseDto.builder()
                .id(99L)
                .name("  Java  ")
                .build();

        CourseDto result = courseService.create(dto);

        assertNotNull(result.getId());
        assertNotEquals(99L, result.getId());
        assertEquals("Java", result.getName());
    }

    @Test
    void createRejectsDuplicateName() {
        courseRepository.save(Course.builder()
                .name("Java")
                .build());

        CourseDto dto = CourseDto.builder()
                .name("  Java  ")
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> courseService.create(dto));
    }

    @Test
    void updateChangesNameAndKeepsId() {
        Course course = courseRepository.save(Course.builder()
                .name("Ancien nom")
                .build());

        CourseDto dto = CourseDto.builder()
                .id(99L)
                .name("  Nouveau nom  ")
                .build();

        CourseDto result = courseService.update(course.getId(), dto);

        assertEquals(course.getId(), result.getId());
        assertEquals("Nouveau nom", result.getName());
        assertEquals("Nouveau nom", courseRepository.findById(course.getId())
                .orElseThrow()
                .getName());
    }

    @Test
    void updateAllowsUnchangedName() {
        Course course = courseRepository.save(Course.builder()
                .name("Java")
                .build());

        CourseDto dto = CourseDto.builder()
                .name("Java")
                .build();

        CourseDto result = courseService.update(course.getId(), dto);

        assertEquals(course.getId(), result.getId());
        assertEquals("Java", result.getName());
    }

    @Test
    void updateRejectsDuplicateName() {
        Course course = courseRepository.save(Course.builder()
                .name("Java")
                .build());
        courseRepository.save(Course.builder()
                .name("Angular")
                .build());

        CourseDto dto = CourseDto.builder()
                .name("Angular")
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> courseService.update(course.getId(), dto));

        assertEquals("Java", courseRepository.findById(course.getId())
                .orElseThrow()
                .getName());
    }

    @Test
    void updateRejectsMissingCourse() {
        CourseDto dto = CourseDto.builder()
                .name("Java")
                .build();

        assertThrows(NoSuchElementException.class,
                () -> courseService.update(42L, dto));
    }

    @Test
    void deleteRemovesExistingCourse() {
        Course course = courseRepository.saveAndFlush(Course.builder()
                .name("Java")
                .build());

        assertDoesNotThrow(() -> courseService.delete(course.getId()));

        assertFalse(courseRepository.existsById(course.getId()));
    }

    @Test
    void deleteRejectsMissingCourse() {
        assertThrows(NoSuchElementException.class,
                () -> courseService.delete(42L));
    }

    @Test
    void deleteThrowsDataIntegrityViolationWhenCourseIsReferenced() {
        Course course = courseRepository.saveAndFlush(Course.builder()
                .name("Java")
                .build());
        Sector sector = Sector.builder()
                .name("Secteur test cours service")
                .build();
        Track track = Track.builder()
                .name("Cursus test cours service")
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

        assertThrows(DataIntegrityViolationException.class,
                () -> courseService.delete(course.getId()));
    }
}
