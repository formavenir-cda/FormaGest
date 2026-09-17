package com.eni.formagest.bll.training;

import com.eni.formagest.bo.training.Course;
import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.bo.training.TrackCourse;
import com.eni.formagest.dal.training.TrackCourseRepository;
import com.eni.formagest.dto.training.TrackCourseDto;
import com.eni.formagest.dto.training.TrackCourseOrderDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrackCourseServiceTest {

    @Autowired
    private TrackCourseService trackCourseService;

    @Autowired
    private TrackCourseRepository trackCourseRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void reorderCoursesUpdatesPositionsAndReturnsOrderedCourses() {
        Track track = saveTrackWithCourses(
                "Service ordre OK",
                "Java",
                "Angular",
                "Spring"
        );
        List<TrackCourse> initialCourses =
                trackCourseRepository.findByTrackIdOrderByPositionAsc(track.getId());

        List<TrackCourseDto> result = trackCourseService.reorderCourses(
                track.getId(),
                List.of(
                        order(initialCourses.get(2).getCourse().getId(), 1),
                        order(initialCourses.get(0).getCourse().getId(), 2),
                        order(initialCourses.get(1).getCourse().getId(), 3)
                )
        );

        assertEquals(3, result.size());
        assertEquals(initialCourses.get(2).getCourse().getId(), result.get(0).getCourseId());
        assertEquals(1, result.get(0).getPosition());
        assertEquals(initialCourses.get(0).getCourse().getId(), result.get(1).getCourseId());
        assertEquals(2, result.get(1).getPosition());
        assertEquals(initialCourses.get(1).getCourse().getId(), result.get(2).getCourseId());
        assertEquals(3, result.get(2).getPosition());
    }

    @Test
    void reorderCoursesRejectsMissingTrack() {
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> trackCourseService.reorderCourses(42L, List.of())
        );

        assertEquals(TrackCourseService.MISSING_TRACK, exception.getMessage());
    }

    @Test
    void reorderCoursesRejectsIncompleteCourseList() {
        Track track = saveTrackWithCourses(
                "Service liste incomplete",
                "Java",
                "Angular"
        );
        List<TrackCourse> courses =
                trackCourseRepository.findByTrackIdOrderByPositionAsc(track.getId());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> trackCourseService.reorderCourses(
                        track.getId(),
                        List.of(order(courses.getFirst().getCourse().getId(), 1))
                )
        );

        assertEquals("La liste des cours est incomplète.", exception.getMessage());
    }

    @Test
    void reorderCoursesRejectsCourseOutsideTrack() {
        Track track = saveTrackWithCourses(
                "Service cours hors cursus",
                "Java",
                "Angular"
        );
        Track otherTrack = saveTrackWithCourses(
                "Service autre cursus",
                "Spring"
        );
        List<TrackCourse> courses =
                trackCourseRepository.findByTrackIdOrderByPositionAsc(track.getId());
        List<TrackCourse> otherCourses =
                trackCourseRepository.findByTrackIdOrderByPositionAsc(otherTrack.getId());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> trackCourseService.reorderCourses(
                        track.getId(),
                        List.of(
                                order(courses.getFirst().getCourse().getId(), 1),
                                order(otherCourses.getFirst().getCourse().getId(), 2)
                        )
                )
        );

        assertEquals("Un cours n’appartient pas à ce cursus.", exception.getMessage());
    }

    @Test
    void reorderCoursesRejectsDuplicatePosition() {
        Track track = saveTrackWithCourses(
                "Service position dupliquee",
                "Java",
                "Angular"
        );
        List<TrackCourse> courses =
                trackCourseRepository.findByTrackIdOrderByPositionAsc(track.getId());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> trackCourseService.reorderCourses(
                        track.getId(),
                        List.of(
                                order(courses.get(0).getCourse().getId(), 1),
                                order(courses.get(1).getCourse().getId(), 1)
                        )
                )
        );

        assertEquals("Deux cours ne peuvent pas avoir la même position.",
                exception.getMessage());
    }

    @Test
    void reorderCoursesRejectsMissingPositionInSequence() {
        Track track = saveTrackWithCourses(
                "Service position manquante",
                "Java",
                "Angular"
        );
        List<TrackCourse> courses =
                trackCourseRepository.findByTrackIdOrderByPositionAsc(track.getId());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> trackCourseService.reorderCourses(
                        track.getId(),
                        List.of(
                                order(courses.get(0).getCourse().getId(), 1),
                                order(courses.get(1).getCourse().getId(), 3)
                        )
                )
        );

        assertEquals("Les positions doivent aller de 1 au dernier rang.",
                exception.getMessage());
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

    private TrackCourseOrderDto order(Long courseId, int position) {
        return TrackCourseOrderDto.builder()
                .courseId(courseId)
                .position(position)
                .build();
    }
}
