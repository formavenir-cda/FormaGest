package com.eni.formagest.dto.enrollment;

import com.eni.formagest.bo.enrollment.EnrollmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CohortEnrollmentDto {

    private Long id;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus enrollmentStatus;

    // Renseignés uniquement si l'inscription a été annulée
    private LocalDateTime cancelledDate;
    private String cancelledReason;
    private Long cancelledById;

    private Long studentId;
    private Long createdById;
    private Long cohortId;
}
