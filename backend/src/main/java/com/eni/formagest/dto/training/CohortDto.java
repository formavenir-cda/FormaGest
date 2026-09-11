package com.eni.formagest.dto.training;

import com.eni.formagest.bo.training.CohortStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CohortDto {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private CohortStatus status;
    private Long trackId;

    @Builder.Default
    private List<ScheduledCourseDto> scheduledCourses = new ArrayList<>();
}
