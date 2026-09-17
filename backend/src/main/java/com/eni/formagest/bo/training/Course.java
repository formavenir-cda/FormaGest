package com.eni.formagest.bo.training;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@ToString
@Entity
@Table(name = "COURSE")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COURSE_ID")
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "DURATION_IN_DAYS", nullable = false)
    private int durationInDays;

    @OneToMany(mappedBy = "course")
    @Builder.Default
    private List<TrackCourse> trackCourses = new ArrayList<>();
}
