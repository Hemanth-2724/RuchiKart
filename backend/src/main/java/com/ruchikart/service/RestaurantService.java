package com.ruchikart.service;

import com.ruchikart.dao.RestaurantDAO;
import com.ruchikart.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RestaurantService {

    @Autowired
    private RestaurantDAO restaurantDAO;

    public List<Restaurant> findAll() {
        return restaurantDAO.findAll();
    }

    public List<Restaurant> findAllForAdmin() {
        return restaurantDAO.findAllForAdmin();
    }

    public Restaurant findById(int restaurantId) {
        return restaurantDAO.findById(restaurantId);
    }

    public int findIdByOwnerId(int ownerId) {
        return restaurantDAO.findIdByOwnerId(ownerId);
    }

    public Restaurant create(Restaurant restaurant) {
        return restaurantDAO.create(restaurant);
    }

    public boolean update(Restaurant restaurant) {
        return restaurantDAO.update(restaurant);
    }

    public boolean toggleActive(int restaurantId, boolean isActive) {
        return restaurantDAO.toggleActive(restaurantId, isActive);
    }
}
