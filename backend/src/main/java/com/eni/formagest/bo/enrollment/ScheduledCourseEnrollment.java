package com.eni.formagest.bo.enrollment;

import com.eni.formagest.bo.training.ScheduledCourse;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "SCHEDULED_COURSE_ENROLLMENT")
public class ScheduledCourseEnrollment extends Enrollment {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEDULED_COURSE_ID")
    private ScheduledCourse scheduledCourse;

    @Column(name = "FORCED", nullable = false)
    @ToString.Include
    private boolean force;
}
