package com.ruchikart.service;

import com.ruchikart.entity.*;
import com.ruchikart.model.User;
import com.ruchikart.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantOwnerRepository ownerRepository;

    @Autowired
    private DeliveryPartnerRepository partnerRepository;

    @Autowired
    private AdminRepository adminRepository;

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
        Optional<CustomerEntity> c = customerRepository.findByUsername(username);
        if (c.isPresent()) return mapCustomer(c.get());

        Optional<RestaurantOwnerEntity> o = ownerRepository.findByUsername(username);
        if (o.isPresent()) return mapOwner(o.get());

        Optional<DeliveryPartnerEntity> d = partnerRepository.findByUsername(username);
        if (d.isPresent()) return mapDelivery(d.get());

        Optional<AdminEntity> a = adminRepository.findByUsername(username);
        if (a.isPresent()) return mapAdmin(a.get());

        return null;
    }

    public User findByEmail(String email) {
        Optional<CustomerEntity> c = customerRepository.findByEmail(email);
        if (c.isPresent()) return mapCustomer(c.get());

        Optional<RestaurantOwnerEntity> o = ownerRepository.findByEmail(email);
        if (o.isPresent()) return mapOwner(o.get());

        Optional<DeliveryPartnerEntity> d = partnerRepository.findByEmail(email);
        if (d.isPresent()) return mapDelivery(d.get());

        Optional<AdminEntity> a = adminRepository.findByEmail(email);
        if (a.isPresent()) return mapAdmin(a.get());

        return null;
    }

    public User findById(int userId) {
        if (userId >= 4000000) {
            return adminRepository.findById(userId).map(this::mapAdmin).orElse(null);
        } else if (userId >= 3000000) {
            return partnerRepository.findById(userId).map(this::mapDelivery).orElse(null);
        } else if (userId >= 2000000) {
            return ownerRepository.findById(userId).map(this::mapOwner).orElse(null);
        } else {
            return customerRepository.findById(userId).map(this::mapCustomer).orElse(null);
        }
    }

    public User create(User user) {
        String role = user.getRole() != null ? user.getRole() : "customer";

        if ("admin".equals(role)) {
            AdminEntity entity = new AdminEntity();
            entity.setUsername(user.getUsername());
            entity.setPassword(user.getPassword());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            entity.setCreatedDate(LocalDateTime.now());
            entity = adminRepository.save(entity);
            user.setUserID(entity.getAdminId());
        } else if ("restaurant_owner".equals(role)) {
            RestaurantOwnerEntity entity = new RestaurantOwnerEntity();
            entity.setUsername(user.getUsername());
            entity.setPassword(user.getPassword());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            entity.setCreatedDate(LocalDateTime.now());
            entity = ownerRepository.save(entity);
            user.setUserID(entity.getOwnerId());
        } else if ("delivery_partner".equals(role)) {
            DeliveryPartnerEntity entity = new DeliveryPartnerEntity();
            entity.setUsername(user.getUsername());
            entity.setPassword(user.getPassword());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            entity.setCreatedDate(LocalDateTime.now());
            entity = partnerRepository.save(entity);
            user.setUserID(entity.getPartnerId());
        } else {
            CustomerEntity entity = new CustomerEntity();
            entity.setUsername(user.getUsername());
            entity.setPassword(user.getPassword());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            entity.setCreatedDate(LocalDateTime.now());
            entity = customerRepository.save(entity);
            user.setUserID(entity.getCustomerId());
        }
        return user;
    }

    public boolean update(User user) {
        int userId = user.getUserID();

        if (userId >= 4000000) {
            Optional<AdminEntity> opt = adminRepository.findById(userId);
            if (opt.isEmpty()) return false;
            AdminEntity entity = opt.get();
            entity.setUsername(user.getUsername());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            adminRepository.save(entity);
            return true;
        } else if (userId >= 3000000) {
            Optional<DeliveryPartnerEntity> opt = partnerRepository.findById(userId);
            if (opt.isEmpty()) return false;
            DeliveryPartnerEntity entity = opt.get();
            entity.setUsername(user.getUsername());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            partnerRepository.save(entity);
            return true;
        } else if (userId >= 2000000) {
            Optional<RestaurantOwnerEntity> opt = ownerRepository.findById(userId);
            if (opt.isEmpty()) return false;
            RestaurantOwnerEntity entity = opt.get();
            entity.setUsername(user.getUsername());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            ownerRepository.save(entity);
            return true;
        } else {
            Optional<CustomerEntity> opt = customerRepository.findById(userId);
            if (opt.isEmpty()) return false;
            CustomerEntity entity = opt.get();
            entity.setUsername(user.getUsername());
            entity.setEmail(user.getEmail());
            entity.setAddress(user.getAddress());
            customerRepository.save(entity);
            return true;
        }
    }

    public List<User> findAll() {
        List<User> allUsers = new ArrayList<>();
        customerRepository.findAll().forEach(c -> allUsers.add(mapCustomer(c)));
        ownerRepository.findAll().forEach(o -> allUsers.add(mapOwner(o)));
        partnerRepository.findAll().forEach(d -> allUsers.add(mapDelivery(d)));
        adminRepository.findAll().forEach(a -> allUsers.add(mapAdmin(a)));
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
        LocalDateTime now = LocalDateTime.now();

        if (userId >= 4000000) {
            Optional<AdminEntity> opt = adminRepository.findById(userId);
            if (opt.isPresent()) {
                AdminEntity entity = opt.get();
                entity.setLastLoginDate(now);
                adminRepository.save(entity);
                return true;
            }
        } else if (userId >= 3000000) {
            Optional<DeliveryPartnerEntity> opt = partnerRepository.findById(userId);
            if (opt.isPresent()) {
                DeliveryPartnerEntity entity = opt.get();
                entity.setLastLoginDate(now);
                partnerRepository.save(entity);
                return true;
            }
        } else if (userId >= 2000000) {
            Optional<RestaurantOwnerEntity> opt = ownerRepository.findById(userId);
            if (opt.isPresent()) {
                RestaurantOwnerEntity entity = opt.get();
                entity.setLastLoginDate(now);
                ownerRepository.save(entity);
                return true;
            }
        } else {
            Optional<CustomerEntity> opt = customerRepository.findById(userId);
            if (opt.isPresent()) {
                CustomerEntity entity = opt.get();
                entity.setLastLoginDate(now);
                customerRepository.save(entity);
                return true;
            }
        }
        return false;
    }

    public boolean delete(int userId) {
        if (userId >= 4000000) {
            if (adminRepository.existsById(userId)) {
                adminRepository.deleteById(userId);
                return true;
            }
        } else if (userId >= 3000000) {
            if (partnerRepository.existsById(userId)) {
                partnerRepository.deleteById(userId);
                return true;
            }
        } else if (userId >= 2000000) {
            if (ownerRepository.existsById(userId)) {
                ownerRepository.deleteById(userId);
                return true;
            }
        } else {
            if (customerRepository.existsById(userId)) {
                customerRepository.deleteById(userId);
                return true;
            }
        }
        return false;
    }
}
