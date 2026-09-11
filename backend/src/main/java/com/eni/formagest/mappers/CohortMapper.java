package com.eni.formagest.mappers;

import com.eni.formagest.bo.training.Cohort;
import com.eni.formagest.dto.training.CohortDto;

import java.util.List;
import java.util.stream.Collectors;

public final class CohortMapper {

    private CohortMapper() {
    }

    public static CohortDto toDto(Cohort cohort) {
        return CohortDto.builder()
                .id(cohort.getId())
                .name(cohort.getName())
                .startDate(cohort.getStartDate())
                .endDate(cohort.getEndDate())
                .status(cohort.getStatus())
                .trackId(cohort.getTrack().getId())
                .scheduledCourses(ScheduledCourseMapper.toDtoList(cohort.getScheduledCourses()))
                .build();
    }

    public static List<CohortDto> toDtoList(List<Cohort> cohorts) {
        return cohorts.stream()
                .map(CohortMapper::toDto)
                .collect(Collectors.toList());
    }
}
