package com.ruchikart.service;

import com.ruchikart.dao.UserDAO;
import com.ruchikart.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public User findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userDAO.findByEmail(email);
    }

    public User findById(int userId) {
        return userDAO.findById(userId);
    }

    public User create(User user) {
        return userDAO.create(user);
    }

    public boolean update(User user) {
        return userDAO.update(user);
    }

    public List<User> findAll() {
        return userDAO.findAll();
    }

    public boolean updateRole(int userId, String role) {
        return userDAO.updateRole(userId, role);
    }

    public boolean updateLastLogin(int userId) {
        return userDAO.updateLastLogin(userId);
    }

    public boolean delete(int userId) {
        return userDAO.delete(userId);
    }
}
