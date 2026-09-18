package com.eni.formagest.bll.training;

import com.eni.formagest.bo.training.Cohort;
import com.eni.formagest.bo.training.CohortStatus;
import com.eni.formagest.bo.training.ScheduledCourse;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.bo.training.TrackCourse;
import com.eni.formagest.bo.users.Teacher;
import com.eni.formagest.bo.users.User;
import com.eni.formagest.bo.users.UserRole;
import com.eni.formagest.dal.training.CohortRepository;
import com.eni.formagest.dal.training.ScheduledCourseRepository;
import com.eni.formagest.dal.training.TrackCourseRepository;
import com.eni.formagest.dal.training.TrackRepository;
import com.eni.formagest.dal.users.UserRepository;
import com.eni.formagest.dto.training.CohortDto;
import com.eni.formagest.dto.training.CourseTeacherAssignmentDto;
import com.eni.formagest.mappers.CohortMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class CohortServiceImpl implements CohortService {

    private final CohortRepository cohortRepository;
    private final TrackRepository trackRepository;
    private final TrackCourseRepository trackCourseRepository;
    private final ScheduledCourseRepository scheduledCourseRepository;
    private final UserRepository userRepository;

    public CohortServiceImpl(
            CohortRepository cohortRepository,
            TrackRepository trackRepository,
            TrackCourseRepository trackCourseRepository,
            ScheduledCourseRepository scheduledCourseRepository,
            UserRepository userRepository) {
        this.cohortRepository = cohortRepository;
        this.trackRepository = trackRepository;
        this.trackCourseRepository = trackCourseRepository;
        this.scheduledCourseRepository = scheduledCourseRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CohortDto> findAll() {
        return CohortMapper.toDtoList(cohortRepository.findAll());
    }

    @Override
    @Transactional
    public CohortDto create(CohortDto dto) {
        String name = dto.getName().strip();

        if (cohortRepository.existsByName(name)) {
            throw new IllegalArgumentException("duplicate");
        }

        Track track = trackRepository.findById(dto.getTrackId())
                .orElseThrow(() -> new NoSuchElementException(MISSING_TRACK));

        if (trackCourseRepository.findByTrackIdOrderByPositionAsc(track.getId()).isEmpty()) {
            throw new IllegalArgumentException(EMPTY_TRACK);
        }

        Cohort cohort = Cohort.builder()
                .name(name)
                .startDate(dto.getStartDate())
                .endDate(dto.getStartDate())
                .status(CohortStatus.UPCOMING)
                .track(track)
                .build();

        Cohort savedCohort = cohortRepository.save(cohort);
        recalculateSchedule(savedCohort, resolveTeacherAssignments(dto.getTeacherAssignments()));

        return CohortMapper.toDto(savedCohort);
    }

    /**
     * Vérifie que chaque formateur choisi existe bien et a le rôle formateur.
     */
    private Map<Long, Teacher> resolveTeacherAssignments(
            List<CourseTeacherAssignmentDto> teacherAssignments) {

        Map<Long, Teacher> teacherByCourseId = new HashMap<>();

        if (teacherAssignments == null) {
            return teacherByCourseId;
        }

        for (CourseTeacherAssignmentDto assignment : teacherAssignments) {
            User user = userRepository.findByIdAndRole(assignment.getTeacherId(), UserRole.TEACHER)
                    .orElseThrow(() -> new NoSuchElementException(MISSING_TEACHER));

            teacherByCourseId.put(assignment.getCourseId(), (Teacher) user);
        }

        return teacherByCourseId;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasInProgressCohortForTrack(Long trackId) {
        return cohortRepository.existsByTrackIdAndStatus(trackId, CohortStatus.IN_PROGRESS);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasInProgressCohortForCourse(Long courseId) {
        return scheduledCourseRepository.existsByCourseIdAndCohort_Status(
                courseId,
                CohortStatus.IN_PROGRESS
        );
    }

    @Override
    @Transactional
    public void recalculateUpcomingCohortsForTrack(Long trackId) {
        cohortRepository.findByTrackIdAndStatus(trackId, CohortStatus.UPCOMING)
                .forEach(this::recalculateSchedule);
    }

    /**
     * Recalcule le planning d’une promotion à partir de la composition actuelle de son cursus,
     * en conservant les formateurs déjà affectés à ses cours.
     */
    private void recalculateSchedule(Cohort cohort) {
        recalculateSchedule(cohort, teachersByCourseId(cohort));
    }

    /**
     * Recalcule le planning d’une promotion à partir de la composition actuelle de son cursus,
     * en affectant à chaque cours le formateur fourni (s’il y en a un).
     */
    private void recalculateSchedule(Cohort cohort, Map<Long, Teacher> teacherByCourseId) {
        List<TrackCourse> trackCourses =
                trackCourseRepository.findByTrackIdOrderByPositionAsc(cohort.getTrack().getId());

        cohort.getScheduledCourses().clear();
        cohortRepository.saveAndFlush(cohort);

        LocalDate nextCourseStart = nextWorkingDay(cohort.getStartDate());
        LocalDate cohortEndDate = nextCourseStart;

        for (TrackCourse trackCourse : trackCourses) {
            LocalDate courseStartDate = nextWorkingDay(nextCourseStart);
            LocalDate courseEndDate = addWorkingDays(
                    courseStartDate,
                    trackCourse.getCourse().getDurationInDays() - 1
            );

            cohort.getScheduledCourses().add(ScheduledCourse.builder()
                    .cohort(cohort)
                    .course(trackCourse.getCourse())
                    .teacher(teacherByCourseId.get(trackCourse.getCourse().getId()))
                    .startDate(courseStartDate)
                    .endDate(courseEndDate)
                    .build());

            cohortEndDate = courseEndDate;
            nextCourseStart = nextWorkingDay(courseEndDate.plusDays(1));
        }

        cohort.setEndDate(cohortEndDate);
        cohortRepository.save(cohort);
    }

    private Map<Long, Teacher> teachersByCourseId(Cohort cohort) {
        Map<Long, Teacher> teacherByCourseId = new HashMap<>();

        for (ScheduledCourse scheduledCourse : cohort.getScheduledCourses()) {
            if (scheduledCourse.getTeacher() != null) {
                teacherByCourseId.put(scheduledCourse.getCourse().getId(), scheduledCourse.getTeacher());
            }
        }

        return teacherByCourseId;
    }

    private LocalDate addWorkingDays(LocalDate date, int daysToAdd) {
        LocalDate result = date;

        for (int daysAdded = 0; daysAdded < daysToAdd; daysAdded++) {
            result = nextWorkingDay(result.plusDays(1));
        }

        return result;
    }

    private LocalDate nextWorkingDay(LocalDate date) {
        LocalDate result = date;

        while (isWeekend(result)) {
            result = result.plusDays(1);
        }

        return result;
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return DayOfWeek.SATURDAY.equals(dayOfWeek)
                || DayOfWeek.SUNDAY.equals(dayOfWeek);
    }
}
