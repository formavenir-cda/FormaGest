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
@Table(name = "SECTOR")
public class Sector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SECTOR_ID")
    @ToString.Include
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    @ToString.Include
    private String name;

    @OneToMany(mappedBy = "sector")
    @Builder.Default
    private List<Track> tracks = new ArrayList<>();
}
