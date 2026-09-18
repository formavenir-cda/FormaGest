package com.eni.formagest.dto.calendar;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEntryDto {

    private Long scheduledCourseId;
    private String courseName;
    private LocalDate startDate;
    private LocalDate endDate;
}
