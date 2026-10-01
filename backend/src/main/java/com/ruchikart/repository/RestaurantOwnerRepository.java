package com.ruchikart.repository;

import com.ruchikart.entity.RestaurantOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantOwnerRepository extends JpaRepository<RestaurantOwnerEntity, Integer> {
    Optional<RestaurantOwnerEntity> findByUsername(String username);
    Optional<RestaurantOwnerEntity> findByEmail(String email);
}
