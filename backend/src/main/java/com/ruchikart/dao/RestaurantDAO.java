package com.ruchikart.dao;

import com.ruchikart.entity.RestaurantEntity;
import com.ruchikart.model.Restaurant;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RestaurantDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

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
        return r;
    }

    public List<Restaurant> findAll() {
        Session session = getSession();
        List<RestaurantEntity> entities = session.createQuery(
                "FROM RestaurantEntity WHERE active = true ORDER BY rating DESC", RestaurantEntity.class
        ).getResultList();
        List<Restaurant> list = new ArrayList<>();
        entities.forEach(e -> list.add(mapEntity(e)));
        return list;
    }

    public List<Restaurant> findAllForAdmin() {
        Session session = getSession();
        List<RestaurantEntity> entities = session.createQuery(
                "FROM RestaurantEntity ORDER BY restaurantId ASC", RestaurantEntity.class
        ).getResultList();
        List<Restaurant> list = new ArrayList<>();
        entities.forEach(e -> list.add(mapEntity(e)));
        return list;
    }

    public Restaurant findById(int restaurantId) {
        Session session = getSession();
        RestaurantEntity entity = session.get(RestaurantEntity.class, restaurantId);
        return mapEntity(entity);
    }

    public int findIdByOwnerId(int ownerId) {
        Session session = getSession();
        List<Integer> list = session.createQuery(
                "SELECT r.restaurantId FROM RestaurantEntity r WHERE r.ownerId = :oId", Integer.class
        ).setParameter("oId", ownerId).getResultList();
        return !list.isEmpty() ? list.get(0) : 1;
    }

    public Restaurant create(Restaurant restaurant) {
        Session session = getSession();
        RestaurantEntity entity = new RestaurantEntity();
        entity.setName(restaurant.getName());
        entity.setCuisineType(restaurant.getCuisineType());
        entity.setDeliveryTime(restaurant.getDeliveryTime());
        entity.setAddress(restaurant.getAddress());
        entity.setRating(restaurant.getRating());
        entity.setActive(restaurant.isActive());
        entity.setImagePath(restaurant.getImagePath());
        entity.setVeg(restaurant.isVeg());
        
        session.persist(entity);
        restaurant.setRestaurantID(entity.getRestaurantId());
        return restaurant;
    }

    public boolean update(Restaurant restaurant) {
        Session session = getSession();
        RestaurantEntity entity = session.get(RestaurantEntity.class, restaurant.getRestaurantID());
        if (entity == null) return false;

        entity.setName(restaurant.getName());
        entity.setCuisineType(restaurant.getCuisineType());
        entity.setDeliveryTime(restaurant.getDeliveryTime());
        entity.setAddress(restaurant.getAddress());
        entity.setRating(restaurant.getRating());
        entity.setImagePath(restaurant.getImagePath());
        entity.setVeg(restaurant.isVeg());
        entity.setActive(restaurant.isActive());

        session.merge(entity);
        return true;
    }

    public boolean toggleActive(int restaurantId, boolean isActive) {
        Session session = getSession();
        RestaurantEntity entity = session.get(RestaurantEntity.class, restaurantId);
        if (entity == null) return false;

        entity.setActive(isActive);
        session.merge(entity);
        return true;
    }
}
