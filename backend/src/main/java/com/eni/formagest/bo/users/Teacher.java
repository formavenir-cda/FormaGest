package com.eni.formagest.bo.users;

import com.eni.formagest.bo.training.Sector;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@Entity
@Table(name = "TEACHER")
public class Teacher extends User{

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "SECTOR_ID")
    private Sector sector;
}
