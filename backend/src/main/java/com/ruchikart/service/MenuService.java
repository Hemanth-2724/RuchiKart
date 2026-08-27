package com.ruchikart.service;

import com.ruchikart.dao.MenuDAO;
import com.ruchikart.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuService {

    @Autowired
    private MenuDAO menuDAO;

    public List<Menu> findByRestaurantId(int restaurantId) {
        return menuDAO.findByRestaurantId(restaurantId);
    }

    public List<Menu> findAvailableByRestaurantId(int restaurantId) {
        return menuDAO.findAvailableByRestaurantId(restaurantId);
    }

    public Menu findById(int menuId) {
        return menuDAO.findById(menuId);
    }

    public Menu create(Menu menu) {
        return menuDAO.create(menu);
    }

    public boolean update(Menu menu) {
        return menuDAO.update(menu);
    }

    public boolean delete(int menuId) {
        return menuDAO.delete(menuId);
    }

    public boolean toggleAvailability(int menuId, boolean isAvailable) {
        return menuDAO.toggleAvailability(menuId, isAvailable);
    }

    public int countByRestaurantId(int restaurantId) {
        return menuDAO.countByRestaurantId(restaurantId);
    }
}
