package com.ruchikart.dao;

import com.ruchikart.entity.*;
import com.ruchikart.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    private User mapCustomer(CustomerEntity c) {
        if (c == null) return null;
        User user = new User();
        user.setUserID(c.getCustomerId());
        user.setUsername(c.getUsername());
        user.setPassword(c.getPassword());
        user.setEmail(c.getEmail());
        user.setAddress(c.getAddress());
        user.setRole("customer");
        user.setCreatedDate(c.getCreatedDate());
        user.setLastLoginDate(c.getLastLoginDate());
        return user;
    }

    private User mapOwner(RestaurantOwnerEntity o) {
        if (o == null) return null;
        User user = new User();
        user.setUserID(o.getOwnerId());
        user.setUsername(o.getUsername());
        user.setPassword(o.getPassword());
        user.setEmail(o.getEmail());
        user.setAddress(o.getAddress());
        user.setRole("restaurant_owner");
        user.setCreatedDate(o.getCreatedDate());
        user.setLastLoginDate(o.getLastLoginDate());
        return user;
    }

    private User mapDelivery(DeliveryPartnerEntity d) {
        if (d == null) return null;
        User user = new User();
        user.setUserID(d.getPartnerId());
        user.setUsername(d.getUsername());
        user.setPassword(d.getPassword());
        user.setEmail(d.getEmail());
        user.setAddress(d.getAddress());
        user.setRole("delivery_partner");
        user.setCreatedDate(d.getCreatedDate());
        user.setLastLoginDate(d.getLastLoginDate());
        return user;
    }

    private User mapAdmin(AdminEntity a) {
        if (a == null) return null;
        User user = new User();
        user.setUserID(a.getAdminId());
        user.setUsername(a.getUsername());
        user.setPassword(a.getPassword());
        user.setEmail(a.getEmail());
        user.setAddress(a.getAddress());
        user.setRole("admin");
        user.setCreatedDate(a.getCreatedDate());
        user.setLastLoginDate(a.getLastLoginDate());
        return user;
    }

    public User findByUsername(String username) {
        Session session = getSession();

        List<CustomerEntity> customers = session.createQuery("FROM CustomerEntity WHERE username = :u", CustomerEntity.class)
                .setParameter("u", username).getResultList();
        if (!customers.isEmpty()) return mapCustomer(customers.get(0));

        List<RestaurantOwnerEntity> owners = session.createQuery("FROM RestaurantOwnerEntity WHERE username = :u", RestaurantOwnerEntity.class)
                .setParameter("u", username).getResultList();
        if (!owners.isEmpty()) return mapOwner(owners.get(0));

        List<DeliveryPartnerEntity> partners = session.createQuery("FROM DeliveryPartnerEntity WHERE username = :u", DeliveryPartnerEntity.class)
                .setParameter("u", username).getResultList();
        if (!partners.isEmpty()) return mapDelivery(partners.get(0));

        List<AdminEntity> admins = session.createQuery("FROM AdminEntity WHERE username = :u", AdminEntity.class)
                .setParameter("u", username).getResultList();
        if (!admins.isEmpty()) return mapAdmin(admins.get(0));

        return null;
    }

    public User findByEmail(String email) {
        Session session = getSession();

        List<CustomerEntity> customers = session.createQuery("FROM CustomerEntity WHERE email = :e", CustomerEntity.class)
                .setParameter("e", email).getResultList();
        if (!customers.isEmpty()) return mapCustomer(customers.get(0));

        List<RestaurantOwnerEntity> owners = session.createQuery("FROM RestaurantOwnerEntity WHERE email = :e", RestaurantOwnerEntity.class)
                .setParameter("e", email).getResultList();
        if (!owners.isEmpty()) return mapOwner(owners.get(0));

        List<DeliveryPartnerEntity> partners = session.createQuery("FROM DeliveryPartnerEntity WHERE email = :e", DeliveryPartnerEntity.class)
                .setParameter("e", email).getResultList();
        if (!partners.isEmpty()) return mapDelivery(partners.get(0));

        List<AdminEntity> admins = session.createQuery("FROM AdminEntity WHERE email = :e", AdminEntity.class)
                .setParameter("e", email).getResultList();
        if (!admins.isEmpty()) return mapAdmin(admins.get(0));

        return null;
    }

    public User findById(int userId) {
        Session session = getSession();

        if (userId >= 4000000) {
            AdminEntity admin = session.get(AdminEntity.class, userId);
            if (admin != null) return mapAdmin(admin);
        } else if (userId >= 3000000) {
            DeliveryPartnerEntity partner = session.get(DeliveryPartnerEntity.class, userId);
            if (partner != null) return mapDelivery(partner);
        } else if (userId >= 2000000) {
            RestaurantOwnerEntity owner = session.get(RestaurantOwnerEntity.class, userId);
            if (owner != null) return mapOwner(owner);
        } else {
            CustomerEntity customer = session.get(CustomerEntity.class, userId);
            if (customer != null) return mapCustomer(customer);
        }
        return null;
    }

    public User create(User user) {
        Session session = getSession();
        String role = user.getRole() != null ? user.getRole() : "customer";

        if ("admin".equals(role)) {
            AdminEntity entity = new AdminEntity();
            entity.setUsername(user.getUsername());
            entity.setPassword(user.getPassword());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            entity.setCreatedDate(LocalDateTime.now());
            session.persist(entity);
            user.setUserID(entity.getAdminId());
        } else if ("restaurant_owner".equals(role)) {
            RestaurantOwnerEntity entity = new RestaurantOwnerEntity();
            entity.setUsername(user.getUsername());
            entity.setPassword(user.getPassword());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            entity.setCreatedDate(LocalDateTime.now());
            session.persist(entity);
            user.setUserID(entity.getOwnerId());
        } else if ("delivery_partner".equals(role)) {
            DeliveryPartnerEntity entity = new DeliveryPartnerEntity();
            entity.setUsername(user.getUsername());
            entity.setPassword(user.getPassword());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            entity.setCreatedDate(LocalDateTime.now());
            session.persist(entity);
            user.setUserID(entity.getPartnerId());
        } else {
            CustomerEntity entity = new CustomerEntity();
            entity.setUsername(user.getUsername());
            entity.setPassword(user.getPassword());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            entity.setCreatedDate(LocalDateTime.now());
            session.persist(entity);
            user.setUserID(entity.getCustomerId());
        }
        return user;
    }

    public boolean update(User user) {
        Session session = getSession();
        int userId = user.getUserID();

        if (userId >= 4000000) {
            AdminEntity entity = session.get(AdminEntity.class, userId);
            if (entity == null) return false;
            entity.setUsername(user.getUsername());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            session.merge(entity);
            return true;
        } else if (userId >= 3000000) {
            DeliveryPartnerEntity entity = session.get(DeliveryPartnerEntity.class, userId);
            if (entity == null) return false;
            entity.setUsername(user.getUsername());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            session.merge(entity);
            return true;
        } else if (userId >= 2000000) {
            RestaurantOwnerEntity entity = session.get(RestaurantOwnerEntity.class, userId);
            if (entity == null) return false;
            entity.setUsername(user.getUsername());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            session.merge(entity);
            return true;
        } else {
            CustomerEntity entity = session.get(CustomerEntity.class, userId);
            if (entity == null) return false;
            entity.setUsername(user.getUsername());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            session.merge(entity);
            return true;
        }
    }

    public List<User> findAll() {
        Session session = getSession();
        List<User> allUsers = new ArrayList<>();

        List<CustomerEntity> customers = session.createQuery("FROM CustomerEntity", CustomerEntity.class).getResultList();
        customers.forEach(c -> allUsers.add(mapCustomer(c)));

        List<RestaurantOwnerEntity> owners = session.createQuery("FROM RestaurantOwnerEntity", RestaurantOwnerEntity.class).getResultList();
        owners.forEach(o -> allUsers.add(mapOwner(o)));

        List<DeliveryPartnerEntity> partners = session.createQuery("FROM DeliveryPartnerEntity", DeliveryPartnerEntity.class).getResultList();
        partners.forEach(d -> allUsers.add(mapDelivery(d)));

        List<AdminEntity> admins = session.createQuery("FROM AdminEntity", AdminEntity.class).getResultList();
        admins.forEach(a -> allUsers.add(mapAdmin(a)));

        allUsers.sort((u1, u2) -> Integer.compare(u1.getUserID(), u2.getUserID()));
        return allUsers;
    }

    public boolean updateRole(int userId, String newRole) {
        User user = findById(userId);
        if (user == null) return false;
        if (user.getRole().equals(newRole)) return true;

        delete(userId);
        user.setRole(newRole);
        create(user);
        return true;
    }

    public boolean updateLastLogin(int userId) {
        Session session = getSession();
        LocalDateTime now = LocalDateTime.now();

        if (userId >= 4000000) {
            AdminEntity entity = session.get(AdminEntity.class, userId);
            if (entity != null) { entity.setLastLoginDate(now); session.merge(entity); return true; }
        } else if (userId >= 3000000) {
            DeliveryPartnerEntity entity = session.get(DeliveryPartnerEntity.class, userId);
            if (entity != null) { entity.setLastLoginDate(now); session.merge(entity); return true; }
        } else if (userId >= 2000000) {
            RestaurantOwnerEntity entity = session.get(RestaurantOwnerEntity.class, userId);
            if (entity != null) { entity.setLastLoginDate(now); session.merge(entity); return true; }
        } else {
            CustomerEntity entity = session.get(CustomerEntity.class, userId);
            if (entity != null) { entity.setLastLoginDate(now); session.merge(entity); return true; }
        }
        return false;
    }

    public boolean delete(int userId) {
        Session session = getSession();
        if (userId >= 4000000) {
            AdminEntity entity = session.get(AdminEntity.class, userId);
            if (entity != null) { session.remove(entity); return true; }
        } else if (userId >= 3000000) {
            DeliveryPartnerEntity entity = session.get(DeliveryPartnerEntity.class, userId);
            if (entity != null) { session.remove(entity); return true; }
        } else if (userId >= 2000000) {
            RestaurantOwnerEntity entity = session.get(RestaurantOwnerEntity.class, userId);
            if (entity != null) { session.remove(entity); return true; }
        } else {
            CustomerEntity entity = session.get(CustomerEntity.class, userId);
            if (entity != null) { session.remove(entity); return true; }
        }
        return false;
    }
}
