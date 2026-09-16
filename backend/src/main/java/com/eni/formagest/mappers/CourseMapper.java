package com.eni.formagest.mappers;

import com.eni.formagest.bo.training.Course;
import com.eni.formagest.bo.training.TrackCourse;
import com.eni.formagest.dto.training.CourseAssociationDto;
import com.eni.formagest.dto.training.CourseDto;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class CourseMapper {

    private CourseMapper() {
    }

    public static CourseDto toDto(Course course) {
        return CourseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .associations(toAssociationDtoList(course.getTrackCourses()))
                .build();
    }

    public static List<CourseDto> toDtoList(List<Course> courses) {
        return courses.stream()
                .map(CourseMapper::toDto)
                .collect(Collectors.toList());
    }

    private static List<CourseAssociationDto> toAssociationDtoList(List<TrackCourse> trackCourses) {
        return trackCourses.stream()
                .sorted(Comparator
                        .comparing((TrackCourse trackCourse) -> trackCourse.getTrack().getSector().getName())
                        .thenComparing(trackCourse -> trackCourse.getTrack().getName())
                        .thenComparingInt(TrackCourse::getPosition))
                .map(trackCourse -> CourseAssociationDto.builder()
                        .sectorId(trackCourse.getTrack().getSector().getId())
                        .sectorName(trackCourse.getTrack().getSector().getName())
                        .trackId(trackCourse.getTrack().getId())
                        .trackName(trackCourse.getTrack().getName())
                        .position(trackCourse.getPosition())
                        .build())
                .collect(Collectors.toList());
    }
}
