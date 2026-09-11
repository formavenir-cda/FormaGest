package com.eni.formagest.dal;

import com.eni.formagest.bo.users.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
