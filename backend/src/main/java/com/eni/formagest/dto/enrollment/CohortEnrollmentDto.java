package com.eni.formagest.dto.enrollment;

import com.eni.formagest.bo.enrollment.EnrollmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CohortEnrollmentDto {

    private Long id;

    // Renseignés par le service, ignorés si présents dans une requête de création
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus enrollmentStatus;
    private Long createdById;

    // Renseignés uniquement si l'inscription a été annulée
    private LocalDateTime cancelledDate;
    private String cancelledReason;
    private Long cancelledById;

    @NotNull(message = "L'élève est obligatoire.")
    private Long studentId;

    @NotNull(message = "La promotion est obligatoire.")
    private Long cohortId;
}
