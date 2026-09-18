package com.eni.formagest.bll.calendar;

import com.eni.formagest.bo.enrollment.CohortEnrollment;
import com.eni.formagest.bo.training.Cohort;
import com.eni.formagest.bo.training.ScheduledCourse;
import com.eni.formagest.dal.enrollment.CohortEnrollmentRepository;
import com.eni.formagest.dal.enrollment.ScheduledCourseEnrollmentRepository;
import com.eni.formagest.dal.training.ScheduledCourseRepository;
import com.eni.formagest.dto.calendar.CalendarEntryDto;
import com.eni.formagest.mappers.CalendarEntryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalendarServiceImpl implements CalendarService {

    private final CohortEnrollmentRepository cohortEnrollmentRepository;
    private final ScheduledCourseEnrollmentRepository scheduledCourseEnrollmentRepository;
    private final ScheduledCourseRepository scheduledCourseRepository;

    public CalendarServiceImpl(
            CohortEnrollmentRepository cohortEnrollmentRepository,
            ScheduledCourseEnrollmentRepository scheduledCourseEnrollmentRepository,
            ScheduledCourseRepository scheduledCourseRepository) {
        this.cohortEnrollmentRepository = cohortEnrollmentRepository;
        this.scheduledCourseEnrollmentRepository = scheduledCourseEnrollmentRepository;
        this.scheduledCourseRepository = scheduledCourseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarEntryDto> getCalendar(Long studentId) {
        Map<Long, ScheduledCourse> scheduledCoursesById = new LinkedHashMap<>();

        cohortEnrollmentRepository.findFirstByStudentIdOrderByEnrollmentDateDesc(studentId)
                .map(CohortEnrollment::getCohort)
                .map(Cohort::getId)
                .map(scheduledCourseRepository::findByCohortId)
                .ifPresent(scheduledCourses ->
                        scheduledCourses.forEach(sc -> scheduledCoursesById.put(sc.getId(), sc)));

        scheduledCourseEnrollmentRepository.findByStudentId(studentId)
                .forEach(enrollment -> {
                    ScheduledCourse scheduledCourse = enrollment.getScheduledCourse();
                    scheduledCoursesById.put(scheduledCourse.getId(), scheduledCourse);
                });

        List<ScheduledCourse> sortedScheduledCourses = scheduledCoursesById.values().stream()
                .sorted(Comparator.comparing(ScheduledCourse::getStartDate))
                .collect(Collectors.toList());

        return CalendarEntryMapper.toDtoList(sortedScheduledCourses);
    }
}
