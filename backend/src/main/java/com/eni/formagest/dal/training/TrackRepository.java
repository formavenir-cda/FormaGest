package com.eni.formagest.dal.training;

import com.eni.formagest.bo.training.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {

    List<Track> findBySectorId(Long sectorId);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
