package com.ruchikart.repository;

import com.ruchikart.entity.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Integer> {
    Optional<AdminEntity> findByUsername(String username);
    Optional<AdminEntity> findByEmail(String email);
}
