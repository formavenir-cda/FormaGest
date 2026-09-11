package com.eni.formagest.mappers;

import com.eni.formagest.bo.enrollment.ScheduledCourseEnrollment;
import com.eni.formagest.dto.enrollment.ScheduledCourseEnrollmentDto;

import java.util.List;
import java.util.stream.Collectors;

public final class ScheduledCourseEnrollmentMapper {

    private ScheduledCourseEnrollmentMapper() {
    }

    public static ScheduledCourseEnrollmentDto toDto(ScheduledCourseEnrollment enrollment) {
        return ScheduledCourseEnrollmentDto.builder()
                .id(enrollment.getId())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .enrollmentStatus(enrollment.getEnrollmentStatus())
                .cancelledDate(enrollment.getCancelledDate())
                .cancelledReason(enrollment.getCancelledReason())
                .cancelledById(enrollment.getCancelledBy() != null ? enrollment.getCancelledBy().getId() : null)
                .forced(enrollment.isForce())
                .justificationForced(enrollment.getJustificationForced())
                .studentId(enrollment.getStudent().getId())
                .createdById(enrollment.getCreatedBy().getId())
                .scheduledCourseId(enrollment.getScheduledCourse().getId())
                .build();
    }

    public static List<ScheduledCourseEnrollmentDto> toDtoList(List<ScheduledCourseEnrollment> enrollments) {
        return enrollments.stream()
                .map(ScheduledCourseEnrollmentMapper::toDto)
                .collect(Collectors.toList());
    }
}
