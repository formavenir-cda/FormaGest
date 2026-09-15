package com.eni.formagest.dto.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorDto {

    private Long id;

    @NotBlank(message = "Le nom de la filière est obligatoire.")
    @Size(max = 255, message = "Le nom ne doit pas dépasser 255 caractères.")
    private String name;
}
