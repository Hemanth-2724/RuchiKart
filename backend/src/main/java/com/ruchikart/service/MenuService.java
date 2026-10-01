package com.ruchikart.service;

import com.ruchikart.entity.MenuEntity;
import com.ruchikart.model.Menu;
import com.ruchikart.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    private Menu mapEntity(MenuEntity e) {
        if (e == null) return null;
        Menu m = new Menu();
        m.setMenuID(e.getMenuId());
        m.setRestaurantID(e.getRestaurantId());
        m.setItemName(e.getItemName());
        m.setDescription(e.getDescription());
        m.setPrice(e.getPrice());
        m.setAvailable(e.isAvailable());
        m.setImagePath(e.getImagePath());
        m.setVeg(e.isVeg());
        return m;
    }

    public List<Menu> findByRestaurantId(int restaurantId) {
        List<MenuEntity> entities = menuRepository.findByRestaurantIdOrderByMenuIdAsc(restaurantId);
        List<Menu> list = new ArrayList<>();
        entities.forEach(e -> list.add(mapEntity(e)));
        return list;
    }

    public List<Menu> findAvailableByRestaurantId(int restaurantId) {
        List<MenuEntity> entities = menuRepository.findByRestaurantIdAndAvailableTrueOrderByMenuIdAsc(restaurantId);
        List<Menu> list = new ArrayList<>();
        entities.forEach(e -> list.add(mapEntity(e)));
        return list;
    }

    public Menu findById(int menuId) {
        return menuRepository.findById(menuId).map(this::mapEntity).orElse(null);
    }

    public Menu create(Menu menu) {
        MenuEntity entity = new MenuEntity();
        entity.setRestaurantId(menu.getRestaurantID());
        entity.setItemName(menu.getItemName());
        entity.setDescription(menu.getDescription());
        entity.setPrice(menu.getPrice());
        entity.setAvailable(menu.isAvailable());
        entity.setImagePath(menu.getImagePath());
        entity.setVeg(menu.isVeg());
        entity = menuRepository.save(entity);
        menu.setMenuID(entity.getMenuId());
        return menu;
    }

    public boolean update(Menu menu) {
        Optional<MenuEntity> opt = menuRepository.findById(menu.getMenuID());
        if (opt.isEmpty()) return false;
        MenuEntity entity = opt.get();
        entity.setItemName(menu.getItemName());
        entity.setDescription(menu.getDescription());
        entity.setPrice(menu.getPrice());
        entity.setAvailable(menu.isAvailable());
        entity.setImagePath(menu.getImagePath());
        entity.setVeg(menu.isVeg());
        menuRepository.save(entity);
        return true;
    }

    public boolean delete(int menuId) {
        if (menuRepository.existsById(menuId)) {
            menuRepository.deleteById(menuId);
            return true;
        }
        return false;
    }

    public boolean toggleAvailability(int menuId, boolean isAvailable) {
        Optional<MenuEntity> opt = menuRepository.findById(menuId);
        if (opt.isEmpty()) return false;
        MenuEntity entity = opt.get();
        entity.setAvailable(isAvailable);
        menuRepository.save(entity);
        return true;
    }

    public int countByRestaurantId(int restaurantId) {
        return menuRepository.countByRestaurantId(restaurantId);
    }
}
