package com.eni.formagest.dal.training;

import com.eni.formagest.bo.training.ScheduledCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduledCourseRepository extends JpaRepository<ScheduledCourse, Long> {

    List<ScheduledCourse> findByCohortId(Long cohortId);

    boolean existsByTeacherId(Long teacherId);
}