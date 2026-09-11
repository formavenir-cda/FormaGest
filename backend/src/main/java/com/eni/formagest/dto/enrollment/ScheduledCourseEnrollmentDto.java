package com.eni.formagest.dto.enrollment;

import com.eni.formagest.bo.enrollment.EnrollmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledCourseEnrollmentDto {

    private Long id;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus enrollmentStatus;

    // Renseignés uniquement si l'inscription a été annulée
    private LocalDateTime cancelledDate;
    private String cancelledReason;
    private Long cancelledById;

    // Inscription qui enfreint l'ordre pédagogique du cursus, forcée par la référente (RG10)
    private boolean forced;
    private String justificationForced;

    private Long studentId;
    private Long createdById;
    private Long scheduledCourseId;
}
