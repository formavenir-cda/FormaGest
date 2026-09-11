package com.eni.formagest.bo.enrollment;

import com.eni.formagest.bo.users.AdministrativeManager;
import com.eni.formagest.bo.users.Student;
import com.eni.formagest.bo.users.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@SuperBuilder
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "ENROLLMENT")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ENROLLMENT_ID")
    @ToString.Include
    private Long id;

    @Column(name = "ENROLLMENT_DATE", nullable = false)
    @ToString.Include
    private LocalDateTime enrollmentDate;

    @Column(name = "ENROLLMENT_STATUS", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus enrollmentStatus;

    @Column(name = "CANCELLED_DATE")
    @ToString.Include
    private LocalDateTime cancelledDate;

    @Column(name = "CANCELLED_REASON", length = 200)
    private String cancelledReason;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDENT_ID")
    private Student student;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY")
    private AdministrativeManager createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CANCELLED_BY")
    private AdministrativeManager cancelledBy;
}
