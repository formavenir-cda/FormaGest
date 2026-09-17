package com.eni.formagest.dal.enrollment;

import com.eni.formagest.bo.enrollment.ScheduledCourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduledCourseEnrollmentRepository extends JpaRepository<ScheduledCourseEnrollment, Long> {

    boolean existsByStudentIdAndScheduledCourseCourseId(Long studentId, Long courseId);

    List<ScheduledCourseEnrollment> findByStudentId(Long studentId);
}
