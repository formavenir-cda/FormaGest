package com.eni.formagest.dto.training;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledCourseDto {

    private Long id;
    private Long cohortId;
    private Long courseId;
    private Long teacherId;
    private LocalDate startDate;
    private LocalDate endDate;
}
