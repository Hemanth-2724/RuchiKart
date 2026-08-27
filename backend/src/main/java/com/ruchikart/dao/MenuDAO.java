package com.ruchikart.dao;

import com.ruchikart.entity.MenuEntity;
import com.ruchikart.model.Menu;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MenuDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

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
        Session session = getSession();
        List<MenuEntity> entities = session.createQuery(
                "FROM MenuEntity WHERE restaurantId = :rId ORDER BY menuId ASC", MenuEntity.class
        ).setParameter("rId", restaurantId).getResultList();
        List<Menu> list = new ArrayList<>();
        entities.forEach(e -> list.add(mapEntity(e)));
        return list;
    }

    public List<Menu> findAvailableByRestaurantId(int restaurantId) {
        Session session = getSession();
        List<MenuEntity> entities = session.createQuery(
                "FROM MenuEntity WHERE restaurantId = :rId AND available = true ORDER BY menuId ASC", MenuEntity.class
        ).setParameter("rId", restaurantId).getResultList();
        List<Menu> list = new ArrayList<>();
        entities.forEach(e -> list.add(mapEntity(e)));
        return list;
    }

    public Menu findById(int menuId) {
        Session session = getSession();
        MenuEntity entity = session.get(MenuEntity.class, menuId);
        return mapEntity(entity);
    }

    public Menu create(Menu menu) {
        Session session = getSession();
        MenuEntity entity = new MenuEntity();
        entity.setRestaurantId(menu.getRestaurantID());
        entity.setItemName(menu.getItemName());
        entity.setDescription(menu.getDescription());
        entity.setPrice(menu.getPrice());
        entity.setAvailable(menu.isAvailable());
        entity.setImagePath(menu.getImagePath());
        entity.setVeg(menu.isVeg());

        session.persist(entity);
        menu.setMenuID(entity.getMenuId());
        return menu;
    }

    public boolean update(Menu menu) {
        Session session = getSession();
        MenuEntity entity = session.get(MenuEntity.class, menu.getMenuID());
        if (entity == null) return false;

        entity.setItemName(menu.getItemName());
        entity.setDescription(menu.getDescription());
        entity.setPrice(menu.getPrice());
        entity.setAvailable(menu.isAvailable());
        entity.setImagePath(menu.getImagePath());
        entity.setVeg(menu.isVeg());

        session.merge(entity);
        return true;
    }

    public boolean delete(int menuId) {
        Session session = getSession();
        MenuEntity entity = session.get(MenuEntity.class, menuId);
        if (entity == null) return false;

        session.remove(entity);
        return true;
    }

    public boolean toggleAvailability(int menuId, boolean isAvailable) {
        Session session = getSession();
        MenuEntity entity = session.get(MenuEntity.class, menuId);
        if (entity == null) return false;

        entity.setAvailable(isAvailable);
        session.merge(entity);
        return true;
    }

    public int countByRestaurantId(int restaurantId) {
        Session session = getSession();
        Long count = session.createQuery(
                "SELECT COUNT(m) FROM MenuEntity m WHERE m.restaurantId = :rId", Long.class
        ).setParameter("rId", restaurantId).getSingleResult();
        return count != null ? count.intValue() : 0;
    }
}
