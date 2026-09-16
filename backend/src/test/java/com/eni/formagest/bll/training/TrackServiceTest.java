package com.eni.formagest.bll.training;

import com.eni.formagest.bo.training.Cohort;
import com.eni.formagest.bo.training.CohortStatus;
import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.dal.training.SectorRepository;
import com.eni.formagest.dal.training.TrackRepository;
import com.eni.formagest.dto.training.TrackDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TrackServiceTest {

    @Autowired
    private TrackService trackService;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void findAllReturnsTracks() {
        Sector sector = saveSector("Informatique");
        trackRepository.save(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());

        List<TrackDto> result = trackService.findAll();

        assertEquals(1, result.size());
        assertNotNull(result.getFirst().getId());
        assertEquals("Concepteur developpeur", result.getFirst().getName());
        assertEquals(sector.getId(), result.getFirst().getSectorId());
    }

    @Test
    void findBySectorReturnsOnlySectorTracks() {
        Sector informatique = saveSector("Informatique");
        Sector design = saveSector("Design");

        trackRepository.save(Track.builder()
                .name("Concepteur developpeur")
                .sector(informatique)
                .build());
        trackRepository.save(Track.builder()
                .name("Designer UX")
                .sector(design)
                .build());

        List<TrackDto> result = trackService.findBySector(informatique.getId());

        assertEquals(1, result.size());
        assertEquals("Concepteur developpeur", result.getFirst().getName());
        assertEquals(informatique.getId(), result.getFirst().getSectorId());
    }

    @Test
    void findBySectorRejectsMissingSector() {
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> trackService.findBySector(42L)
        );

        assertEquals(TrackService.MISSING_SECTOR, exception.getMessage());
    }

    @Test
    void createTrimsNameIgnoresProvidedIdAndLinksSector() {
        Sector sector = saveSector("Informatique");

        TrackDto dto = TrackDto.builder()
                .id(99L)
                .name("  Concepteur developpeur  ")
                .sectorId(sector.getId())
                .build();

        TrackDto result = trackService.create(dto);

        assertNotNull(result.getId());
        assertNotEquals(99L, result.getId());
        assertEquals("Concepteur developpeur", result.getName());
        assertEquals(sector.getId(), result.getSectorId());

        Track savedTrack = trackRepository.findById(result.getId())
                .orElseThrow();
        assertEquals("Concepteur developpeur", savedTrack.getName());
        assertEquals(sector.getId(), savedTrack.getSector().getId());
    }

    @Test
    void createRejectsDuplicateName() {
        Sector sector = saveSector("Informatique");
        trackRepository.save(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());

        TrackDto dto = TrackDto.builder()
                .name("  Concepteur developpeur  ")
                .sectorId(sector.getId())
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> trackService.create(dto));
    }

    @Test
    void createRejectsMissingSector() {
        TrackDto dto = TrackDto.builder()
                .name("Concepteur developpeur")
                .sectorId(42L)
                .build();

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> trackService.create(dto)
        );

        assertEquals(TrackService.MISSING_SECTOR, exception.getMessage());
    }

    @Test
    void updateChangesNameAndSectorAndKeepsId() {
        Sector oldSector = saveSector("Informatique");
        Sector newSector = saveSector("Design");
        Track track = trackRepository.save(Track.builder()
                .name("Ancien cursus")
                .sector(oldSector)
                .build());

        TrackDto dto = TrackDto.builder()
                .id(99L)
                .name("  Nouveau cursus  ")
                .sectorId(newSector.getId())
                .build();

        TrackDto result = trackService.update(track.getId(), dto);

        assertEquals(track.getId(), result.getId());
        assertEquals("Nouveau cursus", result.getName());
        assertEquals(newSector.getId(), result.getSectorId());

        Track savedTrack = trackRepository.findById(track.getId())
                .orElseThrow();
        assertEquals("Nouveau cursus", savedTrack.getName());
        assertEquals(newSector.getId(), savedTrack.getSector().getId());
    }

    @Test
    void updateAllowsUnchangedName() {
        Sector sector = saveSector("Informatique");
        Track track = trackRepository.save(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());

        TrackDto dto = TrackDto.builder()
                .name("Concepteur developpeur")
                .sectorId(sector.getId())
                .build();

        TrackDto result = trackService.update(track.getId(), dto);

        assertEquals(track.getId(), result.getId());
        assertEquals("Concepteur developpeur", result.getName());
    }

    @Test
    void updateRejectsDuplicateName() {
        Sector sector = saveSector("Informatique");
        Track track = trackRepository.save(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());
        trackRepository.save(Track.builder()
                .name("Designer UX")
                .sector(sector)
                .build());

        TrackDto dto = TrackDto.builder()
                .name("Designer UX")
                .sectorId(sector.getId())
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> trackService.update(track.getId(), dto));

        assertEquals("Concepteur developpeur", trackRepository.findById(track.getId())
                .orElseThrow()
                .getName());
    }

    @Test
    void updateRejectsMissingTrack() {
        Sector sector = saveSector("Informatique");
        TrackDto dto = TrackDto.builder()
                .name("Designer UX")
                .sectorId(sector.getId())
                .build();

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> trackService.update(42L, dto)
        );

        assertEquals(TrackService.MISSING_TRACK, exception.getMessage());
    }

    @Test
    void updateRejectsMissingSector() {
        Sector sector = saveSector("Informatique");
        Track track = trackRepository.save(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());

        TrackDto dto = TrackDto.builder()
                .name("Designer UX")
                .sectorId(42L)
                .build();

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> trackService.update(track.getId(), dto)
        );

        assertEquals(TrackService.MISSING_SECTOR, exception.getMessage());
    }

    @Test
    void deleteRemovesExistingTrack() {
        Sector sector = saveSector("Informatique");
        Track track = trackRepository.saveAndFlush(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());

        assertDoesNotThrow(() -> trackService.delete(track.getId()));

        assertFalse(trackRepository.existsById(track.getId()));
    }

    @Test
    void deleteRejectsMissingTrack() {
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> trackService.delete(42L)
        );

        assertEquals(TrackService.MISSING_TRACK, exception.getMessage());
    }

    @Test
    void deleteThrowsDataIntegrityViolationWhenTrackIsReferenced() {
        Sector sector = saveSector("Informatique");
        Track track = trackRepository.saveAndFlush(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());

        entityManager.persist(Cohort.builder()
                .name("CDA 2026")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .status(CohortStatus.UPCOMING)
                .track(track)
                .build());
        entityManager.flush();
        entityManager.clear();

        assertThrows(DataIntegrityViolationException.class,
                () -> trackService.delete(track.getId()));
    }

    private Sector saveSector(String name) {
        return sectorRepository.save(Sector.builder()
                .name(name)
                .build());
    }
}
