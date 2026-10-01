package com.ruchikart.repository;

import com.ruchikart.entity.DeliveryPartnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartnerEntity, Integer> {
    Optional<DeliveryPartnerEntity> findByUsername(String username);
    Optional<DeliveryPartnerEntity> findByEmail(String email);
}
