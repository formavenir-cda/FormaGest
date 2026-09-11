package com.eni.formagest.dal;

import com.eni.formagest.bo.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
