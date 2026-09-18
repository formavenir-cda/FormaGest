package com.eni.formagest.controllers.training;

import com.eni.formagest.bll.training.CohortService;
import com.eni.formagest.dto.training.CohortDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cohorts")
@PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
public class CohortController {

    private final CohortService cohortService;

    public CohortController(CohortService cohortService) {
        this.cohortService = cohortService;
    }

    @GetMapping
    public List<CohortDto> findAll() {
        return cohortService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CohortDto create(@Valid @RequestBody CohortDto dto) {
        try {
            return cohortService.create(dto);
        } catch (NoSuchElementException e) {
            String message = CohortService.MISSING_TEACHER.equals(e.getMessage())
                    ? "Le formateur sélectionné n'existe pas."
                    : "Ce cursus n'existe pas.";

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message, e);
        } catch (IllegalArgumentException e) {
            if (CohortService.EMPTY_TRACK.equals(e.getMessage())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Le cursus doit contenir au moins un cours.",
                        e
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Une promotion portant ce nom existe déjà.",
                    e
            );
        }
    }
}
