package com.eni.formagest.mappers;

import com.eni.formagest.bo.training.Course;
import com.eni.formagest.dto.training.CourseDto;

import java.util.List;
import java.util.stream.Collectors;

public final class CourseMapper {

    private CourseMapper() {
    }

    public static CourseDto toDto(Course course) {
        return CourseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .build();
    }

    public static List<CourseDto> toDtoList(List<Course> courses) {
        return courses.stream()
                .map(CourseMapper::toDto)
                .collect(Collectors.toList());
    }
}
