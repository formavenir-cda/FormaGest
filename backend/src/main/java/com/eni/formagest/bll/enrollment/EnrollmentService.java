package com.eni.formagest.bll.enrollment;

import com.eni.formagest.dto.enrollment.CohortEnrollmentDto;
import com.eni.formagest.dto.enrollment.ScheduledCourseEnrollmentDto;

import java.util.List;

public interface EnrollmentService {

    CohortEnrollmentDto enrollToCohort(CohortEnrollmentDto dto);

    ScheduledCourseEnrollmentDto enrollToScheduledCourse(ScheduledCourseEnrollmentDto dto);

    // null si l'élève n'est inscrit à aucune promotion
    CohortEnrollmentDto findCohortEnrollmentByStudent(Long studentId);

    List<ScheduledCourseEnrollmentDto> findScheduledCourseEnrollmentsByStudent(Long studentId);

    // Inscriptions à une promotion à venir ou en cours, tous élèves confondus (RG : un élève
    // ne peut pas être inscrit à deux promotions actives à la fois)
    List<CohortEnrollmentDto> findActiveCohortEnrollments();

    List<CohortEnrollmentDto> findCohortEnrollmentsByCohort(Long cohortId);
}
