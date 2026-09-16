package com.eni.formagest.dto.training;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackCourseOrderDto {

    @NotNull(message = "Le cours est obligatoire.")
    private Long courseId;

    @Min(value = 1, message = "La position doit être supérieure à 0.")
    private int position;
}