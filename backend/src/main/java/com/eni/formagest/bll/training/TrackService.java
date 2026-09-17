package com.eni.formagest.bll.training;

import com.eni.formagest.dto.training.TrackDto;

import java.util.List;

public interface TrackService {

    String MISSING_TRACK = "track";
    String MISSING_SECTOR = "sector";
    String TRACK_USED_IN_COHORT = "track-used-in-cohort";

    List<TrackDto> findAll();

    List<TrackDto> findBySector(Long sectorId);

    TrackDto create(TrackDto dto);

    TrackDto update(Long id, TrackDto dto);

    void delete(Long id);
}
