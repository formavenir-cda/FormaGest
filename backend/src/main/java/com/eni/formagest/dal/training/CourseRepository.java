package com.eni.formagest.dal.training;

import com.eni.formagest.bo.training.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
