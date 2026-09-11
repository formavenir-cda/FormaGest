package com.eni.formagest.dal;

import com.eni.formagest.bo.training.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, Long> {
}
