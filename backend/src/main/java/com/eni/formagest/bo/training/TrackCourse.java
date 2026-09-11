package com.eni.formagest.bo.training;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "TRACK_COURSE", uniqueConstraints = {
        @UniqueConstraint( columnNames = {"TRACK_ID", "COURSE_ID"}),
        @UniqueConstraint( columnNames = {"TRACK_ID", "POSITION"})
})
public class TrackCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "TRACK_ID")
    private Track track;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID")
    private Course course;

    @Column(name = "POSITION", nullable = false)
    @ToString.Include
    private int position;
}
