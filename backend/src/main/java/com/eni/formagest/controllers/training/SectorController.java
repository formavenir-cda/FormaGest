package com.eni.formagest.controllers.training;

import com.eni.formagest.bll.training.SectorService;
import com.eni.formagest.dto.training.SectorDto;

import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/sectors")
public class SectorController {

    private final SectorService sectorService;

    public SectorController(SectorService sectorService) {
        this.sectorService = sectorService;
    }

    @GetMapping
    public List<SectorDto> findAll() {
        return sectorService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    public SectorDto create(@Valid @RequestBody SectorDto dto) {
        try {
            return sectorService.create(dto);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Une filière portant ce nom existe déjà.",
                    e
            );
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    public SectorDto update(
            @PathVariable Long id,
            @Valid @RequestBody SectorDto dto) {

        try {
            return sectorService.update(id, dto);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cette filière n’existe pas.",
                    e
            );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Une filière portant ce nom existe déjà.",
                    e
            );
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        try {
            sectorService.delete(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cette filière n’existe pas.",
                    e
            );
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Impossible de supprimer cette filière : "
                            + "elle est encore utilisée par des données associées.",
                    e
            );
        }
    }
}