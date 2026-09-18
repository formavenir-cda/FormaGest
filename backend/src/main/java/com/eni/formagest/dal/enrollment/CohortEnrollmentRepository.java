package com.eni.formagest.dal.enrollment;

import com.eni.formagest.bo.enrollment.CohortEnrollment;
import com.eni.formagest.bo.training.CohortStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CohortEnrollmentRepository extends JpaRepository<CohortEnrollment, Long> {

    // Un élève n'est censé avoir qu'une promotion active ; si plusieurs inscriptions
    // existaient malgré tout, on retient la plus récente plutôt que de planter.
    Optional<CohortEnrollment> findFirstByStudentIdOrderByEnrollmentDateDesc(Long studentId);

    @Query("""
        select case when count(ce) > 0 then true else false end
        from CohortEnrollment ce
        join ce.cohort.scheduledCourses sc
        where ce.student.id = :studentId and sc.course.id = :courseId
        """)
    boolean existsByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    @Query("""
        select case when count(ce) > 0 then true else false end
        from CohortEnrollment ce
        where ce.student.id = :studentId and ce.cohort.status in :statuses
        """)
    boolean existsByStudentIdAndCohortStatusIn(
            @Param("studentId") Long studentId, @Param("statuses") List<CohortStatus> statuses);

    List<CohortEnrollment> findByCohortStatusIn(List<CohortStatus> statuses);

    List<CohortEnrollment> findByCohortId(Long cohortId);
}
