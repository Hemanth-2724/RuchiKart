package com.ruchikart.repository;

import com.ruchikart.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {
    List<MenuEntity> findByRestaurantIdOrderByMenuIdAsc(Integer restaurantId);
    List<MenuEntity> findByRestaurantIdAndAvailableTrueOrderByMenuIdAsc(Integer restaurantId);
    int countByRestaurantId(Integer restaurantId);
}
