package com.eni.formagest.dal;

import com.eni.formagest.bo.users.AdministrativeManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministrativeManagerRepository extends JpaRepository<AdministrativeManager, Long> {
}
