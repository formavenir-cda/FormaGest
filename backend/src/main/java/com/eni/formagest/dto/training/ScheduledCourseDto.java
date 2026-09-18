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
    private String courseName;
    private Long teacherId;
    private String teacherName;
    private LocalDate startDate;
    private LocalDate endDate;
}
