package com.eni.formagest.dto.training;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackCourseDto {

    private Long id;
    private Long trackId;
    private Long courseId;
    private int position;
}
