package com.eni.formagest.bll.enrollment;

import com.eni.formagest.bo.enrollment.CohortEnrollment;
import com.eni.formagest.bo.enrollment.EnrollmentStatus;
import com.eni.formagest.bo.enrollment.ScheduledCourseEnrollment;
import com.eni.formagest.bo.training.Cohort;
import com.eni.formagest.bo.training.ScheduledCourse;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.bo.training.TrackCourse;
import com.eni.formagest.bo.users.AdministrativeManager;
import com.eni.formagest.bo.users.Student;
import com.eni.formagest.bo.users.User;
import com.eni.formagest.dal.enrollment.CohortEnrollmentRepository;
import com.eni.formagest.dal.enrollment.ScheduledCourseEnrollmentRepository;
import com.eni.formagest.dal.training.CohortRepository;
import com.eni.formagest.dal.training.ScheduledCourseRepository;
import com.eni.formagest.dal.training.TrackCourseRepository;
import com.eni.formagest.dal.users.UserRepository;
import com.eni.formagest.dto.enrollment.CohortEnrollmentDto;
import com.eni.formagest.dto.enrollment.ScheduledCourseEnrollmentDto;
import com.eni.formagest.mappers.CohortEnrollmentMapper;
import com.eni.formagest.mappers.ScheduledCourseEnrollmentMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    public static final String MISSING_STUDENT = "student";
    public static final String MISSING_COHORT = "cohort";
    public static final String MISSING_SCHEDULED_COURSE = "scheduledCourse";
    public static final String DUPLICATE_COURSE = "duplicate";
    public static final String PEDAGOGICAL_ORDER = "order";

    private final CohortEnrollmentRepository cohortEnrollmentRepository;
    private final ScheduledCourseEnrollmentRepository scheduledCourseEnrollmentRepository;
    private final CohortRepository cohortRepository;
    private final ScheduledCourseRepository scheduledCourseRepository;
    private final TrackCourseRepository trackCourseRepository;
    private final UserRepository userRepository;

    public EnrollmentServiceImpl(
            CohortEnrollmentRepository cohortEnrollmentRepository,
            ScheduledCourseEnrollmentRepository scheduledCourseEnrollmentRepository,
            CohortRepository cohortRepository,
            ScheduledCourseRepository scheduledCourseRepository,
            TrackCourseRepository trackCourseRepository,
            UserRepository userRepository) {
        this.cohortEnrollmentRepository = cohortEnrollmentRepository;
        this.scheduledCourseEnrollmentRepository = scheduledCourseEnrollmentRepository;
        this.cohortRepository = cohortRepository;
        this.scheduledCourseRepository = scheduledCourseRepository;
        this.trackCourseRepository = trackCourseRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CohortEnrollmentDto enrollToCohort(CohortEnrollmentDto dto) {
        Student student = findStudent(dto.getStudentId());
        Cohort cohort = cohortRepository.findById(dto.getCohortId())
                .orElseThrow(() -> new NoSuchElementException(MISSING_COHORT));

        CohortEnrollment enrollment = CohortEnrollment.builder()
                .student(student)
                .cohort(cohort)
                .createdBy(currentAdministrativeManager())
                .enrollmentDate(LocalDateTime.now())
                .enrollmentStatus(EnrollmentStatus.NEW)
                .build();

        return CohortEnrollmentMapper.toDto(cohortEnrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional
    public ScheduledCourseEnrollmentDto enrollToScheduledCourse(ScheduledCourseEnrollmentDto dto) {
        Student student = findStudent(dto.getStudentId());
        ScheduledCourse scheduledCourse = scheduledCourseRepository.findById(dto.getScheduledCourseId())
                .orElseThrow(() -> new NoSuchElementException(MISSING_SCHEDULED_COURSE));

        Long courseId = scheduledCourse.getCourse().getId();

        if (isCourseAlreadyCovered(student.getId(), courseId)) {
            throw new IllegalArgumentException(DUPLICATE_COURSE);
        }

        if (!dto.isForced() && !isPedagogicalOrderRespected(student.getId(), scheduledCourse)) {
            throw new IllegalStateException(PEDAGOGICAL_ORDER);
        }

        ScheduledCourseEnrollment enrollment = ScheduledCourseEnrollment.builder()
                .student(student)
                .scheduledCourse(scheduledCourse)
                .createdBy(currentAdministrativeManager())
                .enrollmentDate(LocalDateTime.now())
                .enrollmentStatus(EnrollmentStatus.NEW)
                .force(dto.isForced())
                .justificationForced(dto.getJustificationForced())
                .build();

        return ScheduledCourseEnrollmentMapper.toDto(scheduledCourseEnrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional(readOnly = true)
    public CohortEnrollmentDto findCohortEnrollmentByStudent(Long studentId) {
        return cohortEnrollmentRepository.findFirstByStudentIdOrderByEnrollmentDateDesc(studentId)
                .map(CohortEnrollmentMapper::toDto)
                .orElse(null);
    }

    private boolean isCourseAlreadyCovered(Long studentId, Long courseId) {
        return scheduledCourseEnrollmentRepository.existsByStudentIdAndScheduledCourseCourseId(studentId, courseId)
                || cohortEnrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    private boolean isPedagogicalOrderRespected(Long studentId, ScheduledCourse scheduledCourse) {
        Track track = scheduledCourse.getCohort().getTrack();
        Long targetCourseId = scheduledCourse.getCourse().getId();

        List<TrackCourse> trackCourses = trackCourseRepository.findByTrackIdOrderByPositionAsc(track.getId());

        TrackCourse targetTrackCourse = trackCourses.stream()
                .filter(trackCourse -> trackCourse.getCourse().getId().equals(targetCourseId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(MISSING_SCHEDULED_COURSE));

        return trackCourses.stream()
                .filter(trackCourse -> trackCourse.getPosition() < targetTrackCourse.getPosition())
                .allMatch(trackCourse -> isCourseAlreadyCovered(studentId, trackCourse.getCourse().getId()));
    }

    private Student findStudent(Long studentId) {
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException(MISSING_STUDENT));

        if (!(user instanceof Student student)) {
            throw new NoSuchElementException(MISSING_STUDENT);
        }

        return student;
    }

    private AdministrativeManager currentAdministrativeManager() {
        return (AdministrativeManager) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
