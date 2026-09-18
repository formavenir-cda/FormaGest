package com.eni.formagest.bll.training;

import com.eni.formagest.bo.training.CohortStatus;
import com.eni.formagest.bo.training.Course;
import com.eni.formagest.dal.training.CourseRepository;
import com.eni.formagest.dal.training.ScheduledCourseRepository;
import com.eni.formagest.dal.training.TrackCourseRepository;
import com.eni.formagest.dto.training.CourseDto;
import com.eni.formagest.mappers.CourseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ScheduledCourseRepository scheduledCourseRepository;
    private final TrackCourseRepository trackCourseRepository;
    private final CohortService cohortService;

    public CourseServiceImpl(
            CourseRepository courseRepository,
            ScheduledCourseRepository scheduledCourseRepository,
            TrackCourseRepository trackCourseRepository,
            CohortService cohortService
            ) {
        this.courseRepository = courseRepository;
        this.scheduledCourseRepository = scheduledCourseRepository;
        this.trackCourseRepository = trackCourseRepository;
        this.cohortService = cohortService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDto> findAll() {
        List<CourseDto> courses = CourseMapper.toDtoList(courseRepository.findAll());

        Set<Long> courseIdsWithInProgressCohort = new HashSet<>(
                scheduledCourseRepository.findCourseIdsByCohortStatus(CohortStatus.IN_PROGRESS)
        );
        Set<Long> courseIdsUsedInAnyCohort = new HashSet<>(
                scheduledCourseRepository.findCourseIdsUsedInAnyCohort()
        );

        courses.forEach(course -> {
            course.setHasInProgressCohort(courseIdsWithInProgressCohort.contains(course.getId()));
            course.setUsedInCohort(courseIdsUsedInAnyCohort.contains(course.getId()));
        });

        return courses;
    }

    @Override
    @Transactional
    public CourseDto create(CourseDto dto) {
        String name = dto.getName().strip();

        if (courseRepository.existsByName(name)) {
            throw new IllegalArgumentException();
        }

        Course course = new Course();
        course.setName(name);
        course.setDurationInDays(dto.getDurationInDays());

        Course savedCourse = courseRepository.save(course);

        return CourseMapper.toDto(savedCourse);
    }

    @Override
    @Transactional
    public CourseDto update(Long id, CourseDto dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        if (cohortService.hasInProgressCohortForCourse(id)) {
            throw new IllegalStateException(COURSE_USED_IN_COHORT);
        }

        String name = dto.getName().strip();

        if (courseRepository.existsByNameAndIdNot(name, id)) {
            throw new IllegalArgumentException();
        }

        course.setName(name);
        course.setDurationInDays(dto.getDurationInDays());

        Course savedCourse = courseRepository.save(course);

        recalculateAffectedCohorts(id);

        return CourseMapper.toDto(savedCourse);
    }

    /**
     * Répercute une modification de cours sur le planning des promotions
     * à venir des cursus qui l’utilisent.
     */
    private void recalculateAffectedCohorts(Long courseId) {
        trackCourseRepository.findByCourseId(courseId).stream()
                .map(trackCourse -> trackCourse.getTrack().getId())
                .distinct()
                .forEach(cohortService::recalculateUpcomingCohortsForTrack);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        if (scheduledCourseRepository.existsByCourseId(id)) {
            throw new IllegalStateException(COURSE_USED_IN_COHORT);
        }

        courseRepository.delete(course);
        courseRepository.flush();
    }
}
