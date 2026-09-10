package com.eni.formagest.bo.training;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@ToString
@Entity
@Table(name = "SECTOR")
public class Sector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SECTOR_ID")
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;
}
