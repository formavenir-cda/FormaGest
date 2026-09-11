package com.eni.formagest.mappers;

import com.eni.formagest.bo.enrollment.CohortEnrollment;
import com.eni.formagest.dto.enrollment.CohortEnrollmentDto;

import java.util.List;
import java.util.stream.Collectors;

public final class CohortEnrollmentMapper {

    private CohortEnrollmentMapper() {
    }

    public static CohortEnrollmentDto toDto(CohortEnrollment enrollment) {
        return CohortEnrollmentDto.builder()
                .id(enrollment.getId())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .studentId(enrollment.getStudent().getId())
                .cohortId(enrollment.getCohort().getId())
                .build();
    }

    public static List<CohortEnrollmentDto> toDtoList(List<CohortEnrollment> enrollments) {
        return enrollments.stream()
                .map(CohortEnrollmentMapper::toDto)
                .collect(Collectors.toList());
    }
}
