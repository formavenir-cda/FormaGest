package com.eni.formagest.dal.users;

import com.eni.formagest.bo.users.User;
import com.eni.formagest.bo.users.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);
}
