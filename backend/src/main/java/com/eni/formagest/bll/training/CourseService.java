package com.eni.formagest.bll.training;

import com.eni.formagest.dto.training.CourseDto;

import java.util.List;

public interface CourseService {

    String COURSE_USED_IN_COHORT = "course-used-in-cohort";

    List<CourseDto> findAll();

    CourseDto create(CourseDto dto);

    CourseDto update(Long id, CourseDto dto);

    void delete(Long id);
}
