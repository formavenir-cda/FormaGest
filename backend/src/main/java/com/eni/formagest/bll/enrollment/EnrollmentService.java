package com.eni.formagest.bll.enrollment;

import com.eni.formagest.dto.enrollment.CohortEnrollmentDto;
import com.eni.formagest.dto.enrollment.ScheduledCourseEnrollmentDto;

public interface EnrollmentService {

    CohortEnrollmentDto enrollToCohort(CohortEnrollmentDto dto);

    ScheduledCourseEnrollmentDto enrollToScheduledCourse(ScheduledCourseEnrollmentDto dto);

    // null si l'élève n'est inscrit à aucune promotion
    CohortEnrollmentDto findCohortEnrollmentByStudent(Long studentId);
}
