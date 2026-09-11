package com.eni.formagest.mappers;

import com.eni.formagest.bo.training.TrackCourse;
import com.eni.formagest.dto.training.TrackCourseDto;

import java.util.List;
import java.util.stream.Collectors;

public final class TrackCourseMapper {

    private TrackCourseMapper() {
    }

    public static TrackCourseDto toDto(TrackCourse trackCourse) {
        return TrackCourseDto.builder()
                .id(trackCourse.getId())
                .trackId(trackCourse.getTrack().getId())
                .courseId(trackCourse.getCourse().getId())
                .position(trackCourse.getPosition())
                .build();
    }

    public static List<TrackCourseDto> toDtoList(List<TrackCourse> trackCourses) {
        return trackCourses.stream()
                .map(TrackCourseMapper::toDto)
                .collect(Collectors.toList());
    }
}
