package com.eni.formagest.mappers;

import com.eni.formagest.bo.training.ScheduledCourse;
import com.eni.formagest.dto.calendar.CalendarEntryDto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class CalendarEntryMapper {

    private CalendarEntryMapper() {
    }

    public static CalendarEntryDto toDto(ScheduledCourse scheduledCourse) {
        return CalendarEntryDto.builder()
                .scheduledCourseId(scheduledCourse.getId())
                .courseName(scheduledCourse.getCourse().getName())
                .startDate(scheduledCourse.getStartDate())
                .endDate(scheduledCourse.getEndDate())
                .build();
    }

    public static List<CalendarEntryDto> toDtoList(Collection<ScheduledCourse> scheduledCourses) {
        return scheduledCourses.stream()
                .map(CalendarEntryMapper::toDto)
                .collect(Collectors.toList());
    }
}
