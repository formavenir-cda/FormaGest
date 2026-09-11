package com.eni.formagest.bo.enrollment;

import com.eni.formagest.bo.users.Student;
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDENT_ID")
    private Student student;
}
