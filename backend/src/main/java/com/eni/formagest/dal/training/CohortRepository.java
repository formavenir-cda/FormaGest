package com.eni.formagest.dal.training;

import com.eni.formagest.bo.training.Cohort;
import com.eni.formagest.bo.training.CohortStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CohortRepository extends JpaRepository<Cohort, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsByTrackId(Long trackId);

    boolean existsByTrackIdAndStatus(Long trackId, CohortStatus status);

    List<Cohort> findByTrackIdAndStatus(Long trackId, CohortStatus status);

    @Query("select distinct cohort.track.id from Cohort cohort where cohort.status = :status")
    List<Long> findTrackIdsByStatus(CohortStatus status);
}
