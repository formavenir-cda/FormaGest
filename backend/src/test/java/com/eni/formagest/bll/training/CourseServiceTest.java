package com.eni.formagest.bll.training;

import com.eni.formagest.bo.training.Course;
import com.eni.formagest.bo.training.Cohort;
import com.eni.formagest.bo.training.CohortStatus;
import com.eni.formagest.bo.training.ScheduledCourse;
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

import java.time.LocalDate;
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
                .durationInDays(5)
                .build());

        List<CourseDto> result = courseService.findAll();

        assertEquals(1, result.size());
        assertEquals(course.getId(), result.getFirst().getId());
        assertEquals("Java", result.getFirst().getName());
        assertEquals(5, result.getFirst().getDurationInDays());
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
                .durationInDays(5)
                .build();

        CourseDto result = courseService.create(dto);

        assertNotNull(result.getId());
        assertNotEquals(99L, result.getId());
        assertEquals("Java", result.getName());
        assertEquals(5, result.getDurationInDays());
    }

    @Test
    void createRejectsDuplicateName() {
        courseRepository.save(Course.builder()
                .name("Java")
                .durationInDays(5)
                .build());

        CourseDto dto = CourseDto.builder()
                .name("  Java  ")
                .durationInDays(5)
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> courseService.create(dto));
    }

    @Test
    void updateChangesNameAndKeepsId() {
        Course course = courseRepository.save(Course.builder()
                .name("Ancien nom")
                .durationInDays(5)
                .build());

        CourseDto dto = CourseDto.builder()
                .id(99L)
                .name("  Nouveau nom  ")
                .durationInDays(7)
                .build();

        CourseDto result = courseService.update(course.getId(), dto);

        assertEquals(course.getId(), result.getId());
        assertEquals("Nouveau nom", result.getName());
        assertEquals(7, result.getDurationInDays());
        assertEquals("Nouveau nom", courseRepository.findById(course.getId())
                .orElseThrow()
                .getName());
        assertEquals(7, courseRepository.findById(course.getId())
                .orElseThrow()
                .getDurationInDays());
    }

    @Test
    void updateAllowsUnchangedName() {
        Course course = courseRepository.save(Course.builder()
                .name("Java")
                .durationInDays(5)
                .build());

        CourseDto dto = CourseDto.builder()
                .name("Java")
                .durationInDays(5)
                .build();

        CourseDto result = courseService.update(course.getId(), dto);

        assertEquals(course.getId(), result.getId());
        assertEquals("Java", result.getName());
    }

    @Test
    void updateRejectsDuplicateName() {
        Course course = courseRepository.save(Course.builder()
                .name("Java")
                .durationInDays(5)
                .build());
        courseRepository.save(Course.builder()
                .name("Angular")
                .durationInDays(5)
                .build());

        CourseDto dto = CourseDto.builder()
                .name("Angular")
                .durationInDays(5)
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
                .durationInDays(5)
                .build();

        assertThrows(NoSuchElementException.class,
                () -> courseService.update(42L, dto));
    }

    @Test
    void updateRejectsCourseUsedInStartedCohort() {
        Course course = saveScheduledCourse(
                "Java cours utilise update",
                CohortStatus.IN_PROGRESS
        );
        CourseDto dto = CourseDto.builder()
                .name("Java modifie")
                .durationInDays(7)
                .build();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> courseService.update(course.getId(), dto)
        );

        assertEquals(CourseService.COURSE_USED_IN_COHORT, exception.getMessage());
    }

    @Test
    void updateAllowsCourseUsedInUpcomingCohortAndRecalculatesSchedule() {
        Course course = saveScheduledCourse(
                "Java cours utilise a venir",
                CohortStatus.UPCOMING
        );
        CourseDto dto = CourseDto.builder()
                .name("Java modifie")
                .durationInDays(10)
                .build();

        CourseDto result = courseService.update(course.getId(), dto);

        assertEquals("Java modifie", result.getName());
        assertEquals(10, result.getDurationInDays());

        ScheduledCourse scheduledCourse = findScheduledCourseForCourse(course.getId());

        assertEquals(LocalDate.of(2026, 9, 14), scheduledCourse.getEndDate());
    }

    private ScheduledCourse findScheduledCourseForCourse(Long courseId) {
        return entityManager.createQuery(
                        "select scheduledCourse from ScheduledCourse scheduledCourse "
                                + "where scheduledCourse.course.id = :courseId",
                        ScheduledCourse.class
                )
                .setParameter("courseId", courseId)
                .getSingleResult();
    }

    @Test
    void deleteRemovesExistingCourse() {
        Course course = courseRepository.saveAndFlush(Course.builder()
                .name("Java")
                .durationInDays(5)
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
    void deleteRejectsCourseUsedInCohort() {
        Course course = saveScheduledCourse("Java cours utilise delete", CohortStatus.UPCOMING);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> courseService.delete(course.getId())
        );

        assertEquals(CourseService.COURSE_USED_IN_COHORT, exception.getMessage());
    }

    @Test
    void deleteThrowsDataIntegrityViolationWhenCourseIsReferenced() {
        Course course = courseRepository.saveAndFlush(Course.builder()
                .name("Java")
                .durationInDays(5)
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

    private Course saveScheduledCourse(String courseName, CohortStatus status) {
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
                .status(status)
                .track(track)
                .build();

        entityManager.persist(sector);
        entityManager.persist(track);
        entityManager.persist(course);
        entityManager.persist(TrackCourse.builder()
                .track(track)
                .course(course)
                .position(1)
                .build());
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
