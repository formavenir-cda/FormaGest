package com.eni.formagest.dal.training;

import com.eni.formagest.bo.training.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CohortRepository extends JpaRepository<Cohort, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}