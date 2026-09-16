package com.eni.formagest.controllers.training;

import com.eni.formagest.bll.training.CourseService;
import com.eni.formagest.bll.training.TrackCourseService;
import com.eni.formagest.dto.training.CourseDto;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final TrackCourseService trackCourseService;

    public CourseController(
            CourseService courseService,
            TrackCourseService trackCourseService) {
        this.courseService = courseService;
        this.trackCourseService = trackCourseService;
    }

    @GetMapping
    public List<CourseDto> findAll() {
        return courseService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    public CourseDto create(@Valid @RequestBody CourseDto dto) {
        try {
            return courseService.create(dto);
        } catch (IllegalArgumentException e) {
            throw duplicateCourse(e);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    public CourseDto update(
            @PathVariable Long id,
            @Valid @RequestBody CourseDto dto) {

        try {
            return courseService.update(id, dto);
        } catch (NoSuchElementException e) {
            throw notFound(e);
        } catch (IllegalArgumentException e) {
            throw duplicateCourse(e);
        }
    }

    @PutMapping("/{id}/tracks")
    public CourseDto updateTracks(
            @PathVariable Long id,
            @RequestBody List<Long> trackIds) {

        try {
            return trackCourseService.updateCourseTracks(id, trackIds);
        } catch (NoSuchElementException e) {
            throw notFound(e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
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
            courseService.delete(id);
        } catch (NoSuchElementException e) {
            throw notFound(e);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Impossible de supprimer ce cours : "
                            + "il est encore utilisé par des données associées.",
                    e
            );
        }
    }

    private ResponseStatusException duplicateCourse(IllegalArgumentException e) {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Un cours portant ce nom existe déjà.",
                e
        );
    }

    private ResponseStatusException notFound(NoSuchElementException e) {
        String message = TrackCourseService.MISSING_TRACK.equals(e.getMessage())
                ? "Ce cursus n’existe pas."
                : "Ce cours n’existe pas.";

        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                message,
                e
        );
    }
}
