package com.eni.formagest.dal.training;

import com.eni.formagest.bo.training.Sector;
import org.springframework.data.jpa.repository.JpaRepository;



public interface SectorRepository extends JpaRepository<Sector, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);


}
