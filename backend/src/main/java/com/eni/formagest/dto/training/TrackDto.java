package com.eni.formagest.dto.training;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackDto {

    private Long id;
    private String name;
    private Long sectorId;

    @Builder.Default
    private List<TrackCourseDto> courses = new ArrayList<>();
}
