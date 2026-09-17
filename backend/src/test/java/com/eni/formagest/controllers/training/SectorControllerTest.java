package com.eni.formagest.controllers.training;

import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.training.Track;
import com.eni.formagest.dal.training.SectorRepository;
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

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SectorRepository sectorRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void findAllReturnsOkWithSectors() throws Exception {
        Sector sector = sectorRepository.save(Sector.builder()
                .name("Design")
                .build());
        entityManager.persist(Track.builder()
                .name("Designer UX")
                .sector(sector)
                .build());
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/sectors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(sector.getId()))
                .andExpect(jsonPath("$[0].name").value("Design"))
                .andExpect(jsonPath("$[0].trackCount").value(1));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void findAllReturnsOkForStudentRole() throws Exception {
        mockMvc.perform(get("/api/sectors"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createReturnsCreatedWithSector() throws Exception {
        mockMvc.perform(post("/api/sectors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Design"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Design"));
    }

    @ParameterizedTest
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    @ValueSource(strings = {
            "{}",
            "{\"name\": null}",
            "{\"name\": \"\"}",
            "{\"name\": \"   \"}"
    })
    void createRejectsMissingOrBlankName(String body) throws Exception {
        mockMvc.perform(post("/api/sectors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "Le nom de la filière est obligatoire."
                )));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void createRejectsNameLongerThan255Characters() throws Exception {
        String body = "{\"name\":\"" + "a".repeat(256) + "\"}";

        mockMvc.perform(post("/api/sectors")
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
        sectorRepository.save(Sector.builder()
                .name("Design")
                .build());

        mockMvc.perform(post("/api/sectors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Design"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        "Une filière portant ce nom existe déjà."
                ));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateReturnsUpdatedSector() throws Exception {
        Sector sector = sectorRepository.save(Sector.builder()
                .name("Ancien nom")
                .build());

        mockMvc.perform(put("/api/sectors/{id}", sector.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Design"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sector.getId()))
                .andExpect(jsonPath("$.name").value("Design"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateRejectsBlankName() throws Exception {
        Sector sector = sectorRepository.save(Sector.builder()
                .name("Design")
                .build());

        mockMvc.perform(put("/api/sectors/{id}", sector.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "   "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "Le nom de la filière est obligatoire."
                )));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateReturnsNotFoundWithMessage() throws Exception {
        mockMvc.perform(put("/api/sectors/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Design"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cette filière n’existe pas."));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void updateReturnsConflictWithDuplicateMessage() throws Exception {
        Sector sector = sectorRepository.save(Sector.builder()
                .name("Developpement")
                .build());
        sectorRepository.save(Sector.builder()
                .name("Design")
                .build());

        mockMvc.perform(put("/api/sectors/{id}", sector.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Design"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        "Une filière portant ce nom existe déjà."
                ));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void deleteReturnsNoContent() throws Exception {
        Sector sector = sectorRepository.saveAndFlush(Sector.builder()
                .name("Design")
                .build());

        mockMvc.perform(delete("/api/sectors/{id}", sector.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertFalse(sectorRepository.existsById(sector.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void deleteReturnsNotFoundWithMessage() throws Exception {
        mockMvc.perform(delete("/api/sectors/42"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cette filière n’existe pas."));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRATIVE_MANAGER")
    void deleteReturnsConflictWhenSectorIsReferenced() throws Exception {
        Sector sector = sectorRepository.saveAndFlush(Sector.builder()
                .name("Developpement")
                .build());

        entityManager.persist(Track.builder()
                .name("Concepteur developpeur")
                .sector(sector)
                .build());
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(delete("/api/sectors/{id}", sector.getId()))
                .andExpect(status().isConflict())
                .andExpect(content().string(
                        "Impossible de supprimer cette filière : "
                                + "elle est encore utilisée par des données associées."
                ));
    }
}
