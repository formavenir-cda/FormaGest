package com.eni.formagest.dto.training;

import com.eni.formagest.bo.training.CohortStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Le nom de la promotion est obligatoire.")
    @Size(max = 255, message = "Le nom ne doit pas dépasser 255 caractères.")
    private String name;

    @NotNull(message = "La date de début est obligatoire.")
    private LocalDate startDate;

    private LocalDate endDate;

    private CohortStatus status;

    @NotNull(message = "Le cursus est obligatoire.")
    private Long trackId;

    @Builder.Default
    private List<ScheduledCourseDto> scheduledCourses = new ArrayList<>();
}
