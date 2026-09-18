package com.eni.formagest.dto.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackDto {

    private Long id;

    @NotBlank(message = "Le nom du cursus est obligatoire.")
    @Size(max = 255, message = "Le nom ne doit pas dépasser 255 caractères.")
    private String name;

    @NotNull(message = "La filière est obligatoire.")
    private Long sectorId;

    @Builder.Default
    private List<TrackCourseDto> courses = new ArrayList<>();

    @Builder.Default
    private Boolean hasInProgressCohort = false;
}
