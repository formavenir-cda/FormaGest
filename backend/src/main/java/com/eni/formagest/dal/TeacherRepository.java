package com.eni.formagest.dal;

import com.eni.formagest.bo.users.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
