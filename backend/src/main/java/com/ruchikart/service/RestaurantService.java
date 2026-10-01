package com.ruchikart.service;

import com.ruchikart.entity.RestaurantEntity;
import com.ruchikart.model.Restaurant;
import com.ruchikart.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant mapEntity(RestaurantEntity e) {
        if (e == null) return null;
        Restaurant r = new Restaurant();
        r.setRestaurantID(e.getRestaurantId());
        r.setName(e.getName());
        r.setCuisineType(e.getCuisineType());
        r.setDeliveryTime(e.getDeliveryTime());
        r.setAddress(e.getAddress());
        r.setRating(e.getRating());
        r.setActive(e.isActive());
        r.setImagePath(e.getImagePath());
        r.setVeg(e.isVeg());
        r.setOwnerID(e.getOwnerId() != null ? e.getOwnerId() : 0);
        return r;
    }

    public List<Restaurant> findAll() {
        List<RestaurantEntity> entities = restaurantRepository.findByActiveTrue();
        List<Restaurant> list = new ArrayList<>();
        entities.forEach(e -> list.add(mapEntity(e)));
        return list;
    }

    public List<Restaurant> findAllForAdmin() {
        List<RestaurantEntity> entities = restaurantRepository.findAll();
        List<Restaurant> list = new ArrayList<>();
        entities.forEach(e -> list.add(mapEntity(e)));
        return list;
    }

    public Restaurant findById(int restaurantId) {
        return restaurantRepository.findById(restaurantId).map(this::mapEntity).orElse(null);
    }

    public int findIdByOwnerId(int ownerId) {
        return restaurantRepository.findByOwnerId(ownerId)
                .map(RestaurantEntity::getRestaurantId)
                .orElse(-1);
    }

    public Restaurant create(Restaurant restaurant) {
        RestaurantEntity entity = new RestaurantEntity();
        entity.setName(restaurant.getName());
        entity.setCuisineType(restaurant.getCuisineType());
        entity.setDeliveryTime(restaurant.getDeliveryTime());
        entity.setAddress(restaurant.getAddress());
        entity.setRating(restaurant.getRating());
        entity.setActive(restaurant.isActive());
        entity.setImagePath(restaurant.getImagePath());
        entity.setVeg(restaurant.isVeg());
        entity.setOwnerId(restaurant.getOwnerID() > 0 ? restaurant.getOwnerID() : null);
        entity = restaurantRepository.save(entity);
        restaurant.setRestaurantID(entity.getRestaurantId());
        return restaurant;
    }

    public boolean update(Restaurant restaurant) {
        Optional<RestaurantEntity> opt = restaurantRepository.findById(restaurant.getRestaurantID());
        if (opt.isEmpty()) return false;
        RestaurantEntity entity = opt.get();
        entity.setName(restaurant.getName());
        entity.setCuisineType(restaurant.getCuisineType());
        entity.setDeliveryTime(restaurant.getDeliveryTime());
        entity.setAddress(restaurant.getAddress());
        entity.setRating(restaurant.getRating());
        entity.setActive(restaurant.isActive());
        entity.setImagePath(restaurant.getImagePath());
        entity.setVeg(restaurant.isVeg());
        if (restaurant.getOwnerID() > 0) {
            entity.setOwnerId(restaurant.getOwnerID());
        }
        restaurantRepository.save(entity);
        return true;
    }

    public boolean toggleActive(int restaurantId, boolean isActive) {
        Optional<RestaurantEntity> opt = restaurantRepository.findById(restaurantId);
        if (opt.isEmpty()) return false;
        RestaurantEntity entity = opt.get();
        entity.setActive(isActive);
        restaurantRepository.save(entity);
        return true;
    }
}
