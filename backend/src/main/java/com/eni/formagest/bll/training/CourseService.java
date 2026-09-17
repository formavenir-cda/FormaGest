package com.eni.formagest.bll.training;

import com.eni.formagest.bo.training.Course;
import com.eni.formagest.dal.training.CourseRepository;
import com.eni.formagest.dto.training.CourseDto;
import com.eni.formagest.mappers.CourseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CourseService {


    private final CourseRepository courseRepository;

    public CourseService(
            CourseRepository courseRepository
            ) {
        this.courseRepository = courseRepository;

    }

    @Transactional(readOnly = true)
    public List<CourseDto> findAll() {
        return CourseMapper.toDtoList(courseRepository.findAll());
    }

    @Transactional
    public CourseDto create(CourseDto dto) {
        String name = dto.getName().strip();

        if (courseRepository.existsByName(name)) {
            throw new IllegalArgumentException();
        }

        Course course = new Course();
        course.setName(name);
        course.setDurationInDays(dto.getDurationInDays());

        Course savedCourse = courseRepository.save(course);

        return CourseMapper.toDto(savedCourse);
    }

    @Transactional
    public CourseDto update(Long id, CourseDto dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        String name = dto.getName().strip();

        if (courseRepository.existsByNameAndIdNot(name, id)) {
            throw new IllegalArgumentException();
        }

        course.setName(name);
        course.setDurationInDays(dto.getDurationInDays());

        Course savedCourse = courseRepository.save(course);

        return CourseMapper.toDto(savedCourse);
    }

    @Transactional
    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        courseRepository.delete(course);
        courseRepository.flush();
    }


}
