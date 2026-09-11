package com.eni.formagest.dto.enrollment;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CohortEnrollmentDto {

    private Long id;
    private LocalDateTime enrollmentDate;
    private Long studentId;
    private Long cohortId;
}
