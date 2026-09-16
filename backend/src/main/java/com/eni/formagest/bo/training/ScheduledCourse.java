package com.eni.formagest.bo.training;

import com.eni.formagest.bo.users.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "SCHEDULED_COURSE", uniqueConstraints = {
        @UniqueConstraint( columnNames = {"COHORT_ID", "COURSE_ID"})
})
public class ScheduledCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "COHORT_ID")
    private Cohort cohort;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEACHER_ID")
    private Teacher teacher;

    @Column(name = "START_DATE")
    @ToString.Include
    private LocalDate startDate;

    @Column(name = "END_DATE")
    @ToString.Include
    private LocalDate endDate;
}
