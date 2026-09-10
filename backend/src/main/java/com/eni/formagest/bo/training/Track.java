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
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "TRACK")
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRACK_ID")
    @ToString.Include
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    @ToString.Include
    private String name;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position")
    @Builder.Default
    private List<TrackCourse> courses = new ArrayList<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "SECTOR_ID")
    private Sector sector;
}
