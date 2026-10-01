package com.ruchikart.repository;

import com.ruchikart.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Integer> {
    List<RestaurantEntity> findByActiveTrue();
    Optional<RestaurantEntity> findByOwnerId(Integer ownerId);
}
