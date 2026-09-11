package com.eni.formagest.bo.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;


@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@Entity
@Table(name = "STUDENT")
public class Student extends User{

    @Column(name = "BIRTH_DATE", nullable = false)
    private LocalDate birthDate;
}
