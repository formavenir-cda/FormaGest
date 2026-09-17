package com.eni.formagest.bll.enrollment;

import com.eni.formagest.dal.enrollment.EnrollmentRepository;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }
}
