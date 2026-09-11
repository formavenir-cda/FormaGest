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
                .enrollmentStatus(enrollment.getEnrollmentStatus())
                .cancelledDate(enrollment.getCancelledDate())
                .cancelledReason(enrollment.getCancelledReason())
                .cancelledById(enrollment.getCancelledBy() != null ? enrollment.getCancelledBy().getId() : null)
                .studentId(enrollment.getStudent().getId())
                .createdById(enrollment.getCreatedBy().getId())
                .cohortId(enrollment.getCohort().getId())
                .build();
    }

    public static List<CohortEnrollmentDto> toDtoList(List<CohortEnrollment> enrollments) {
        return enrollments.stream()
                .map(CohortEnrollmentMapper::toDto)
                .collect(Collectors.toList());
    }
}
