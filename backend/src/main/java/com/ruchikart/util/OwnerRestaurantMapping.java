package com.ruchikart.util;

import com.ruchikart.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OwnerRestaurantMapping {

    private static RestaurantService restaurantService;

    @Autowired
    public void setRestaurantService(RestaurantService service) {
        OwnerRestaurantMapping.restaurantService = service;
    }

    public static int getRestaurantId(int userId) {
        if (restaurantService != null) {
            return restaurantService.findIdByOwnerId(userId);
        }
        return 1;
    }

    public static boolean hasMapping(int userId) {
        return true;
    }

    public static void addMapping(int userId, int restaurantId) {
    }
}
