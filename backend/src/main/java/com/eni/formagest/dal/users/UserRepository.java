package com.eni.formagest.dal.users;

import com.eni.formagest.bo.users.User;
import com.eni.formagest.bo.users.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM student WHERE id = :id", nativeQuery = true)
    void deleteStudentRow(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM teacher WHERE id = :id", nativeQuery = true)
    void deleteTeacherRow(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM administrative_manager WHERE id = :id", nativeQuery = true)
    void deleteAdministrativeManagerRow(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM administrator WHERE id = :id", nativeQuery = true)
    void deleteAdministratorRow(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO student (id, birth_date) VALUES (:id, :birthDate)", nativeQuery = true)
    void insertStudentRow(@Param("id") Long id, @Param("birthDate") LocalDate birthDate);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO teacher (id, sector_id) VALUES (:id, :sectorId)", nativeQuery = true)
    void insertTeacherRow(@Param("id") Long id, @Param("sectorId") Long sectorId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO administrative_manager (id) VALUES (:id)", nativeQuery = true)
    void insertAdministrativeManagerRow(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO administrator (id) VALUES (:id)", nativeQuery = true)
    void insertAdministratorRow(@Param("id") Long id);
}
