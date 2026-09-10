package com.eni.formagest.bo.training;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "COHORT")
public class Cohort {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COHORT_ID")
    @ToString.Include
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    @ToString.Include
    private String name;

    @Column(name = "START_DATE", nullable = false)
    @ToString.Include
    private LocalDate startDate;

    @Column(name = "END_DATE", nullable = false)
    @ToString.Include
    private LocalDate endDate;

    @Column(name = "STATUS", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private CohortStatus status;

    @OneToMany(mappedBy = "cohort", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ScheduledCourse> scheduledCourses = new ArrayList<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "TRACK_ID")
    private Track track;
}
