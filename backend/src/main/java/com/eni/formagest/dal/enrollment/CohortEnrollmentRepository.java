package com.eni.formagest.dal.enrollment;

import com.eni.formagest.bo.enrollment.CohortEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CohortEnrollmentRepository extends JpaRepository<CohortEnrollment, Long> {
    CohortEnrollment findByStudentId(Long studentId);
}
