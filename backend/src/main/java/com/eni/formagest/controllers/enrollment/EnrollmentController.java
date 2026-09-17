package com.eni.formagest.controllers.enrollment;

import com.eni.formagest.bll.enrollment.EnrollmentService;
import com.eni.formagest.dto.enrollment.CohortEnrollmentDto;
import com.eni.formagest.dto.enrollment.ScheduledCourseEnrollmentDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/cohorts")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    public CohortEnrollmentDto enrollToCohort(@Valid @RequestBody CohortEnrollmentDto dto) {
        try {
            return enrollmentService.enrollToCohort(dto);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundMessage(e.getMessage()), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet élève est déjà inscrit à une promotion en cours ou à venir.",
                    e
            );
        }
    }

    @PostMapping("/scheduled-courses")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    public ScheduledCourseEnrollmentDto enrollToScheduledCourse(@Valid @RequestBody ScheduledCourseEnrollmentDto dto) {
        try {
            return enrollmentService.enrollToScheduledCourse(dto);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, notFoundMessage(e.getMessage()), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet élève est déjà inscrit à ce cours.",
                    e
            );
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cet élève doit d'abord être inscrit aux cours précédents du cursus. "
                            + "Forcez l'inscription si la situation le justifie.",
                    e
            );
        }
    }

    @GetMapping("/students/{studentId}/cohort")
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    public CohortEnrollmentDto findCohortEnrollmentByStudent(@PathVariable Long studentId) {
        return enrollmentService.findCohortEnrollmentByStudent(studentId);
    }

    @GetMapping("/students/{studentId}/scheduled-courses")
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    public List<ScheduledCourseEnrollmentDto> findScheduledCourseEnrollmentsByStudent(@PathVariable Long studentId) {
        return enrollmentService.findScheduledCourseEnrollmentsByStudent(studentId);
    }

    @GetMapping("/students/active-cohort")
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    public List<CohortEnrollmentDto> findActiveCohortEnrollments() {
        return enrollmentService.findActiveCohortEnrollments();
    }

    @GetMapping("/cohorts/{cohortId}/students")
    @PreAuthorize("hasRole('ADMINISTRATIVE_MANAGER')")
    public List<CohortEnrollmentDto> findCohortEnrollmentsByCohort(@PathVariable Long cohortId) {
        return enrollmentService.findCohortEnrollmentsByCohort(cohortId);
    }

    private String notFoundMessage(String code) {
        return switch (code) {
            case "student" -> "Cet élève n'existe pas.";
            case "cohort" -> "Cette promotion n'existe pas.";
            case "scheduledCourse" -> "Ce cours planifié n'existe pas.";
            default -> "Ressource introuvable.";
        };
    }
}
