package com.eni.formagest.dto.training;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseTeacherAssignmentDto {

    @NotNull(message = "Le cours est obligatoire.")
    private Long courseId;

    @NotNull(message = "Le formateur est obligatoire.")
    private Long teacherId;
}
