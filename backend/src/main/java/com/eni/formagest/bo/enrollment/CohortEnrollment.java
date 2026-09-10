package com.eni.formagest.bo.enrollment;

import com.eni.formagest.bo.training.Cohort;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "COHORT_ENROLLMENT")
public class CohortEnrollment extends Enrollment {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "COHORT_ID")
    private Cohort cohort;
}
