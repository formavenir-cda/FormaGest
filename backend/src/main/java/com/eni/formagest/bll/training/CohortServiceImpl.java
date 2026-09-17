package com.eni.formagest.bll.training;

import com.eni.formagest.bo.training.Cohort;
import com.eni.formagest.bo.training.CohortStatus;
import com.eni.formagest.bo.training.ScheduledCourse;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.bo.training.TrackCourse;
import com.eni.formagest.dal.training.CohortRepository;
import com.eni.formagest.dal.training.ScheduledCourseRepository;
import com.eni.formagest.dal.training.TrackCourseRepository;
import com.eni.formagest.dal.training.TrackRepository;
import com.eni.formagest.dto.training.CohortDto;
import com.eni.formagest.mappers.CohortMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CohortServiceImpl implements CohortService {

    private final CohortRepository cohortRepository;
    private final TrackRepository trackRepository;
    private final TrackCourseRepository trackCourseRepository;
    private final ScheduledCourseRepository scheduledCourseRepository;

    public CohortServiceImpl(
            CohortRepository cohortRepository,
            TrackRepository trackRepository,
            TrackCourseRepository trackCourseRepository,
            ScheduledCourseRepository scheduledCourseRepository) {
        this.cohortRepository = cohortRepository;
        this.trackRepository = trackRepository;
        this.trackCourseRepository = trackCourseRepository;
        this.scheduledCourseRepository = scheduledCourseRepository;
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

        List<TrackCourse> trackCourses =
                trackCourseRepository.findByTrackIdOrderByPositionAsc(track.getId());

        if (trackCourses.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_TRACK);
        }

        List<ScheduledCourse> scheduledCourses = new ArrayList<>();
        LocalDate nextCourseStart = nextWorkingDay(dto.getStartDate());
        LocalDate cohortEndDate = nextCourseStart;

        Cohort cohort = Cohort.builder()
                .name(name)
                .startDate(dto.getStartDate())
                .endDate(cohortEndDate)
                .status(CohortStatus.UPCOMING)
                .track(track)
                .build();

        Cohort savedCohort = cohortRepository.save(cohort);

        for (TrackCourse trackCourse : trackCourses) {
            LocalDate courseStartDate = nextWorkingDay(nextCourseStart);
            LocalDate courseEndDate = addWorkingDays(
                    courseStartDate,
                    trackCourse.getCourse().getDurationInDays() - 1
            );

            scheduledCourses.add(ScheduledCourse.builder()
                    .cohort(savedCohort)
                    .course(trackCourse.getCourse())
                    .startDate(courseStartDate)
                    .endDate(courseEndDate)
                    .build());

            cohortEndDate = courseEndDate;
            nextCourseStart = nextWorkingDay(courseEndDate.plusDays(1));
        }

        savedCohort.setEndDate(cohortEndDate);

        scheduledCourseRepository.saveAll(scheduledCourses);
        savedCohort.setScheduledCourses(scheduledCourses);

        return CohortMapper.toDto(savedCohort);
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
