package com.eni.formagest.mappers;

import com.eni.formagest.bo.training.ScheduledCourse;
import com.eni.formagest.dto.training.ScheduledCourseDto;

import java.util.List;
import java.util.stream.Collectors;

public final class ScheduledCourseMapper {

    private ScheduledCourseMapper() {
    }

    public static ScheduledCourseDto toDto(ScheduledCourse scheduledCourse) {
        return ScheduledCourseDto.builder()
                .id(scheduledCourse.getId())
                .cohortId(scheduledCourse.getCohort().getId())
                .courseId(scheduledCourse.getCourse().getId())
                .courseName(scheduledCourse.getCourse().getName())
                .teacherId(scheduledCourse.getTeacher() != null ? scheduledCourse.getTeacher().getId() : null)
                .startDate(scheduledCourse.getStartDate())
                .endDate(scheduledCourse.getEndDate())
                .build();
    }

    public static List<ScheduledCourseDto> toDtoList(List<ScheduledCourse> scheduledCourses) {
        return scheduledCourses.stream()
                .map(ScheduledCourseMapper::toDto)
                .collect(Collectors.toList());
    }
}
