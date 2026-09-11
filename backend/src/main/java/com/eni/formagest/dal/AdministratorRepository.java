package com.eni.formagest.dal;

import com.eni.formagest.bo.users.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
}
