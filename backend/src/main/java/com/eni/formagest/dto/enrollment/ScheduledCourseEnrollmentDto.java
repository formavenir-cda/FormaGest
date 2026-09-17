package com.eni.formagest.dto.enrollment;

import com.eni.formagest.bo.enrollment.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledCourseEnrollmentDto {

    private Long id;

    // Renseignés par le service, ignorés si présents dans une requête de création
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus enrollmentStatus;
    private Long createdById;

    // Renseignés uniquement si l'inscription a été annulée
    private LocalDateTime cancelledDate;
    private String cancelledReason;
    private Long cancelledById;

    // Inscription qui enfreint l'ordre pédagogique du cursus, forcée par la référente (RG10)
    private boolean forced;

    @Size(max = 200, message = "La justification ne doit pas dépasser 200 caractères.")
    private String justificationForced;

    @NotNull(message = "L'élève est obligatoire.")
    private Long studentId;

    @NotNull(message = "Le cours planifié est obligatoire.")
    private Long scheduledCourseId;
}
