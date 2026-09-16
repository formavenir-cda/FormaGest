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

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CohortService {

    public static final String MISSING_TRACK = "track";

    private final CohortRepository cohortRepository;
    private final TrackRepository trackRepository;
    private final TrackCourseRepository trackCourseRepository;
    private final ScheduledCourseRepository scheduledCourseRepository;

    public CohortService(
            CohortRepository cohortRepository,
            TrackRepository trackRepository,
            TrackCourseRepository trackCourseRepository,
            ScheduledCourseRepository scheduledCourseRepository) {
        this.cohortRepository = cohortRepository;
        this.trackRepository = trackRepository;
        this.trackCourseRepository = trackCourseRepository;
        this.scheduledCourseRepository = scheduledCourseRepository;
    }

    @Transactional(readOnly = true)
    public List<CohortDto> findAll() {
        return CohortMapper.toDtoList(cohortRepository.findAll());
    }

    @Transactional
    public CohortDto create(CohortDto dto) {
        String name = dto.getName().strip();

        if (cohortRepository.existsByName(name)) {
            throw new IllegalArgumentException("duplicate");
        }

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("date");
        }

        Track track = trackRepository.findById(dto.getTrackId())
                .orElseThrow(() -> new NoSuchElementException(MISSING_TRACK));

        Cohort cohort = Cohort.builder()
                .name(name)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(CohortStatus.UPCOMING)
                .track(track)
                .build();

        Cohort savedCohort = cohortRepository.save(cohort);

        List<TrackCourse> trackCourses =
                trackCourseRepository.findByTrackIdOrderByPositionAsc(track.getId());

        List<ScheduledCourse> scheduledCourses = trackCourses.stream()
                .map(trackCourse -> ScheduledCourse.builder()
                        .cohort(savedCohort)
                        .course(trackCourse.getCourse())
                        .build())
                .toList();

        scheduledCourseRepository.saveAll(scheduledCourses);
        savedCohort.setScheduledCourses(scheduledCourses);

        return CohortMapper.toDto(savedCohort);
    }
}