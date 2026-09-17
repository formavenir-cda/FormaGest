package com.eni.formagest.dal.enrollment;

import com.eni.formagest.bo.enrollment.ScheduledCourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledCourseEnrollmentRepository extends JpaRepository<ScheduledCourseEnrollment, Long> {
    ScheduledCourseEnrollment findByStudentId(Long studentId);
}
