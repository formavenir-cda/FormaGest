package com.eni.formagest.dto.training;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseAssociationDto {

    private Long sectorId;
    private String sectorName;
    private Long trackId;
    private String trackName;
    private int position;
}
