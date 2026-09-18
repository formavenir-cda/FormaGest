package com.eni.formagest.dal.training;

import com.eni.formagest.bo.training.CohortStatus;
import com.eni.formagest.bo.training.ScheduledCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduledCourseRepository extends JpaRepository<ScheduledCourse, Long> {

    List<ScheduledCourse> findByCohortId(Long cohortId);

    boolean existsByCourseId(Long courseId);

    boolean existsByCourseIdAndCohort_Status(Long courseId, CohortStatus status);

    boolean existsByTeacherId(Long teacherId);

    @Query("select distinct sc.course.id from ScheduledCourse sc where sc.cohort.status = :status")
    List<Long> findCourseIdsByCohortStatus(CohortStatus status);

    @Query("select distinct sc.course.id from ScheduledCourse sc")
    List<Long> findCourseIdsUsedInAnyCohort();
}
