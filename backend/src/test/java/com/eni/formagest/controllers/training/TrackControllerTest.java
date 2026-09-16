package com.eni.formagest.controllers.training;

import com.eni.formagest.bo.training.Cohort;
import com.eni.formagest.bo.training.CohortStatus;
import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.dal.training.SectorRepository;
import com.eni.formagest.dal.training.TrackRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TrackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void findAllReturnsOkWithTracks() throws Exception {
        Sector sector = saveSector("Informatique");
        Track track = trackRepository.save(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());

        mockMvc.perform(get("/api/tracks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(track.getId()))
                .andExpect(jsonPath("$[0].name").value("Concepteur developpeur"))
                .andExpect(jsonPath("$[0].sectorId").value(sector.getId()));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void findAllReturnsForbiddenForUnauthorizedRole() throws Exception {
        mockMvc.perform(get("/api/tracks"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void findBySectorReturnsOkWithSectorTracks() throws Exception {
        Sector informatique = saveSector("Informatique");
        Sector design = saveSector("Design");

        Track track = trackRepository.save(Track.builder()
                .name("Concepteur developpeur")
                .sector(informatique)
                .build());
        trackRepository.save(Track.builder()
                .name("Designer UX")
                .sector(design)
                .build());

        mockMvc.perform(get("/api/tracks")
                        .param("sectorId", informatique.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(track.getId()))
                .andExpect(jsonPath("$[0].sectorId").value(informatique.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void findBySectorReturnsNotFoundWithMessage() throws Exception {
        mockMvc.perform(get("/api/tracks")
                        .param("sectorId", "42"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cette filière n’existe pas."));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createReturnsCreatedWithTrack() throws Exception {
        Sector sector = saveSector("Informatique");

        mockMvc.perform(post("/api/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Concepteur developpeur", "sectorId": %d}
                                """.formatted(sector.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Concepteur developpeur"))
                .andExpect(jsonPath("$.sectorId").value(sector.getId()));
    }

    @ParameterizedTest
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    @ValueSource(strings = {
            "{}",
            "{\"name\": null, \"sectorId\": 1}",
            "{\"name\": \"\", \"sectorId\": 1}",
            "{\"name\": \"   \", \"sectorId\": 1}"
    })
    void createRejectsMissingOrBlankName(String body) throws Exception {
        mockMvc.perform(post("/api/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "Le nom du cursus est obligatoire."
                )));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createRejectsMissingSectorId() throws Exception {
        mockMvc.perform(post("/api/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Concepteur developpeur"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "La filière est obligatoire."
                )));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createRejectsNameLongerThan255Characters() throws Exception {
        Sector sector = saveSector("Informatique");
        String body = """
                {"name":"%s", "sectorId": %d}
                """.formatted("a".repeat(256), sector.getId());

        mockMvc.perform(post("/api/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "Le nom ne doit pas dépasser 255 caractères."
                )));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createReturnsConflictWithDuplicateMessage() throws Exception {
        Sector sector = saveSector("Informatique");
        trackRepository.save(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());

        mockMvc.perform(post("/api/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Concepteur developpeur", "sectorId": %d}
                                """.formatted(sector.getId())))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        "Un cursus portant ce nom existe déjà."
                ));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createReturnsNotFoundWhenSectorDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Concepteur developpeur", "sectorId": 42}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cette filière n’existe pas."));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateReturnsUpdatedTrack() throws Exception {
        Sector oldSector = saveSector("Informatique");
        Sector newSector = saveSector("Design");
        Track track = trackRepository.save(Track.builder()
                .name("Ancien cursus")
                .sector(oldSector)
                .build());

        mockMvc.perform(put("/api/tracks/{id}", track.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Nouveau cursus", "sectorId": %d}
                                """.formatted(newSector.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(track.getId()))
                .andExpect(jsonPath("$.name").value("Nouveau cursus"))
                .andExpect(jsonPath("$.sectorId").value(newSector.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateRejectsBlankName() throws Exception {
        Sector sector = saveSector("Informatique");
        Track track = trackRepository.save(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());

        mockMvc.perform(put("/api/tracks/{id}", track.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "   ", "sectorId": %d}
                                """.formatted(sector.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "Le nom du cursus est obligatoire."
                )));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateReturnsNotFoundWithMessage() throws Exception {
        Sector sector = saveSector("Informatique");

        mockMvc.perform(put("/api/tracks/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Concepteur developpeur", "sectorId": %d}
                                """.formatted(sector.getId())))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Ce cursus n’existe pas."));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateReturnsNotFoundWhenSectorDoesNotExist() throws Exception {
        Sector sector = saveSector("Informatique");
        Track track = trackRepository.save(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());

        mockMvc.perform(put("/api/tracks/{id}", track.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Concepteur developpeur", "sectorId": 42}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cette filière n’existe pas."));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateReturnsConflictWithDuplicateMessage() throws Exception {
        Sector sector = saveSector("Informatique");
        Track track = trackRepository.save(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());
        trackRepository.save(Track.builder()
                .name("Designer UX")
                .sector(sector)
                .build());

        mockMvc.perform(put("/api/tracks/{id}", track.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Designer UX", "sectorId": %d}
                                """.formatted(sector.getId())))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        "Un cursus portant ce nom existe déjà."
                ));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void deleteReturnsNoContent() throws Exception {
        Sector sector = saveSector("Informatique");
        Track track = trackRepository.saveAndFlush(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());

        mockMvc.perform(delete("/api/tracks/{id}", track.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertFalse(trackRepository.existsById(track.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void deleteReturnsNotFoundWithMessage() throws Exception {
        mockMvc.perform(delete("/api/tracks/42"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Ce cursus n’existe pas."));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void deleteReturnsConflictWhenTrackIsReferenced() throws Exception {
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

        mockMvc.perform(delete("/api/tracks/{id}", track.getId()))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        "Impossible de supprimer ce cursus : "
                                + "il est encore utilisé par des données associées."
                ));
    }

    private Sector saveSector(String name) {
        return sectorRepository.save(Sector.builder()
                .name(name)
                .build());
    }
}
