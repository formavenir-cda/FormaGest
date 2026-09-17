package com.eni.formagest.controllers.training;

import com.eni.formagest.bll.training.TrackCourseService;
import com.eni.formagest.dto.training.TrackCourseDto;
import com.eni.formagest.dto.training.TrackCourseOrderDto;
import com.eni.formagest.bll.training.TrackService;
import com.eni.formagest.dto.training.TrackDto;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;
    private final TrackCourseService trackCourseService;

    public TrackController(TrackService trackService, TrackCourseService trackCourseService) {
        this.trackService = trackService;
        this.trackCourseService = trackCourseService;
    }

    @GetMapping
    public List<TrackDto> findAll(
            @RequestParam(required = false) Long sectorId) {
        if (sectorId != null) {
            try {
                return trackService.findBySector(sectorId);
            } catch (NoSuchElementException e) {
                throw notFound(e);
            }
        }

        return trackService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    public TrackDto create(@Valid @RequestBody TrackDto dto) {
        try {
            return trackService.create(dto);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un cursus portant ce nom existe déjà.",
                    e
            );
        } catch (NoSuchElementException e) {
            throw notFound(e);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    public TrackDto update(
            @PathVariable Long id,
            @Valid @RequestBody TrackDto dto) {

        try {
            return trackService.update(id, dto);
        } catch (IllegalStateException e) {
            throw trackUsed(e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un cursus portant ce nom existe déjà.",
                    e
            );
        } catch (NoSuchElementException e) {
            throw notFound(e);
        }
    }

    @PutMapping("/{id}/courses/order")
    public List<TrackCourseDto> reorderCourses(
            @PathVariable Long id,
            @Valid @RequestBody List<TrackCourseOrderDto> order) {
        try {
            return trackCourseService.reorderCourses(id, order);
        } catch (NoSuchElementException e) {
            throw notFound(e);
        } catch (IllegalStateException e) {
            throw trackUsed(e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    e.getMessage(),
                    e
            );
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    public void delete(@PathVariable Long id) {
        try {
            trackService.delete(id);
        } catch (NoSuchElementException e) {
            throw notFound(e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Impossible de supprimer ce cursus : "
                            + "il est encore utilisé par des données associées.",
                    e
            );
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Impossible de supprimer ce cursus : "
                            + "il est encore utilisé par des données associées.",
                    e
            );
        }
    }

    private ResponseStatusException notFound(NoSuchElementException e) {
        String message = TrackService.MISSING_SECTOR.equals(e.getMessage())
                ? "Cette filière n’existe pas."
                : "Ce cursus n’existe pas.";

        return new ResponseStatusException(HttpStatus.NOT_FOUND, message, e);
    }

    private ResponseStatusException trackUsed(RuntimeException e) {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Impossible de modifier ce cursus : il est utilisé dans une promotion.",
                e
        );
    }
}
