package com.eni.formagest.dto.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDto {

    private Long id;

    @NotBlank(message = "Le nom du cours est obligatoire.")
    @Size(max = 255, message = "Le nom ne doit pas dépasser 255 caractères.")
    private String name;

    @Builder.Default
    private List<CourseAssociationDto> associations = new ArrayList<>();
}
