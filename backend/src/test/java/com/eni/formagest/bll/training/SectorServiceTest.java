package com.eni.formagest.bll.training;

import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.dal.training.SectorRepository;
import com.eni.formagest.dto.training.SectorDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SectorServiceTest {

    @Autowired
    private SectorService sectorService;

    @Autowired
    private SectorRepository sectorRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void findAllReturnsSectors() {
        sectorRepository.save(Sector.builder()
                .name("Developpement")
                .build());

        List<SectorDto> result = sectorService.findAll();

        assertEquals(1, result.size());
        assertNotNull(result.getFirst().getId());
        assertEquals("Developpement", result.getFirst().getName());
    }

    @Test
    void findAllReturnsEmptyList() {
        List<SectorDto> result = sectorService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void createTrimsNameAndIgnoresProvidedId() {
        SectorDto dto = SectorDto.builder()
                .id(99L)
                .name("  Developpement  ")
                .build();

        SectorDto result = sectorService.create(dto);

        assertNotNull(result.getId());
        assertNotEquals(99L, result.getId());
        assertEquals("Developpement", result.getName());

        Sector savedSector = sectorRepository.findById(result.getId())
                .orElseThrow();
        assertEquals("Developpement", savedSector.getName());
    }

    @Test
    void createRejectsDuplicateName() {
        sectorRepository.save(Sector.builder()
                .name("Developpement")
                .build());

        SectorDto dto = SectorDto.builder()
                .name("  Developpement  ")
                .build();

        assertThrows(IllegalArgumentException.class, () -> sectorService.create(dto));
    }

    @Test
    void updateChangesNameAndKeepsId() {
        Sector sector = sectorRepository.save(Sector.builder()
                .name("Ancien nom")
                .build());

        SectorDto dto = SectorDto.builder()
                .id(99L)
                .name("  Nouveau nom  ")
                .build();

        SectorDto result = sectorService.update(sector.getId(), dto);

        assertEquals(sector.getId(), result.getId());
        assertEquals("Nouveau nom", result.getName());
        assertEquals("Nouveau nom", sectorRepository.findById(sector.getId())
                .orElseThrow()
                .getName());
    }

    @Test
    void updateAllowsUnchangedName() {
        Sector sector = sectorRepository.save(Sector.builder()
                .name("Developpement")
                .build());

        SectorDto dto = SectorDto.builder()
                .name("Developpement")
                .build();

        SectorDto result = sectorService.update(sector.getId(), dto);

        assertEquals(sector.getId(), result.getId());
        assertEquals("Developpement", result.getName());
    }

    @Test
    void updateRejectsDuplicateName() {
        Sector sector = sectorRepository.save(Sector.builder()
                .name("Developpement")
                .build());
        sectorRepository.save(Sector.builder()
                .name("Design")
                .build());

        SectorDto dto = SectorDto.builder()
                .name("Design")
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> sectorService.update(sector.getId(), dto));

        assertEquals("Developpement", sectorRepository.findById(sector.getId())
                .orElseThrow()
                .getName());
    }

    @Test
    void updateRejectsMissingSector() {
        SectorDto dto = SectorDto.builder()
                .name("Design")
                .build();

        assertThrows(NoSuchElementException.class,
                () -> sectorService.update(42L, dto));
    }

    @Test
    void deleteRemovesExistingSector() {
        Sector sector = sectorRepository.saveAndFlush(Sector.builder()
                .name("Design")
                .build());

        assertDoesNotThrow(() -> sectorService.delete(sector.getId()));

        assertFalse(sectorRepository.existsById(sector.getId()));
    }

    @Test
    void deleteRejectsMissingSector() {
        assertThrows(NoSuchElementException.class,
                () -> sectorService.delete(42L));
    }

    @Test
    void deleteThrowsDataIntegrityViolationWhenSectorIsReferenced() {
        Sector sector = sectorRepository.saveAndFlush(Sector.builder()
                .name("Developpement")
                .build());

        entityManager.persist(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());
        entityManager.flush();
        entityManager.clear();

        assertThrows(DataIntegrityViolationException.class,
                () -> sectorService.delete(sector.getId()));
    }
}
