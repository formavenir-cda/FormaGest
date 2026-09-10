package com.eni.formagest.bo.users;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

}
