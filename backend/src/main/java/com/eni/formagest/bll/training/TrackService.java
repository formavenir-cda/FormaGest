package com.eni.formagest.bll.training;

import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.dal.training.CohortRepository;
import com.eni.formagest.dal.training.SectorRepository;
import com.eni.formagest.dal.training.TrackRepository;
import com.eni.formagest.dto.training.TrackDto;
import com.eni.formagest.mappers.TrackMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TrackService {

    public static final String MISSING_TRACK = "track";
    public static final String MISSING_SECTOR = "sector";
    public static final String TRACK_USED_IN_COHORT = "track-used-in-cohort";

    private final TrackRepository trackRepository;
    private final SectorRepository sectorRepository;
    private final CohortRepository cohortRepository;

    public TrackService(
            TrackRepository trackRepository,
            SectorRepository sectorRepository,
            CohortRepository cohortRepository) {
        this.trackRepository = trackRepository;
        this.sectorRepository = sectorRepository;
        this.cohortRepository = cohortRepository;
    }

    @Transactional(readOnly = true)
    public List<TrackDto> findAll() {
        return TrackMapper.toDtoList(trackRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<TrackDto> findBySector(Long sectorId) {
        if (!sectorRepository.existsById(sectorId)) {
            throw new NoSuchElementException(MISSING_SECTOR);
        }

        return TrackMapper.toDtoList(trackRepository.findBySectorId(sectorId));
    }

    @Transactional
    public TrackDto create(TrackDto dto) {
        String name = dto.getName().strip();

        if (trackRepository.existsByName(name)) {
            throw new IllegalArgumentException();
        }

        Sector sector = sectorRepository.findById(dto.getSectorId())
                .orElseThrow(() -> new NoSuchElementException(MISSING_SECTOR));

        Track track = new Track();
        track.setName(name);
        track.setSector(sector);

        Track savedTrack = trackRepository.save(track);

        return TrackMapper.toDto(savedTrack);
    }

    @Transactional
    public TrackDto update(Long id, TrackDto dto) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(MISSING_TRACK));

        if (cohortRepository.existsByTrackId(id)) {
            throw new IllegalStateException(TRACK_USED_IN_COHORT);
        }

        String name = dto.getName().strip();

        if (trackRepository.existsByNameAndIdNot(name, id)) {
            throw new IllegalArgumentException();
        }

        Sector sector = sectorRepository.findById(dto.getSectorId())
                .orElseThrow(() -> new NoSuchElementException(MISSING_SECTOR));

        track.setName(name);
        track.setSector(sector);

        Track savedTrack = trackRepository.save(track);

        return TrackMapper.toDto(savedTrack);
    }

    @Transactional
    public void delete(Long id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(MISSING_TRACK));

        if (cohortRepository.existsByTrackId(id)) {
            throw new IllegalStateException(TRACK_USED_IN_COHORT);
        }

        trackRepository.delete(track);
        trackRepository.flush();
    }
}
