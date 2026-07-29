package com.inventory.repository;

import com.inventory.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.id = :id")
    Optional<User> findByIdWithRole(@Param("id") Long id);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.role r WHERE " +
           "(:keyword IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:roleName IS NULL OR r.name = :roleName)")
    Page<User> search(@Param("keyword") String keyword, @Param("roleName") String roleName, Pageable pageable);
}