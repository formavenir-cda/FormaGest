package com.eni.formagest.dal.enrollment;

import com.eni.formagest.bo.enrollment.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByStudentId(Long studentId);

    boolean existsByCreatedById(Long administrativeManagerId);

    boolean existsByCancelledById(Long administrativeManagerId);
}
