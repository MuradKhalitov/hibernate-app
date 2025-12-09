package ru.murad.service.impl;

import ru.murad.dao.UserDao;
import ru.murad.exception.DaoException;
import ru.murad.exception.ServiceException;
import ru.murad.model.User;
import ru.murad.service.UserService;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User createUser(String name, String email, int age) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        try {
            return userDao.save(user);
        } catch (DaoException e) {
            throw new ServiceException("Failed to create user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User updateUser(long id, String name, String email, int age) {

        Optional<User> optional = userDao.findById(id);
        if (optional.isEmpty()) {
            throw new ServiceException("User with id=" + id + " not found");
        }

        User user = optional.get();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        try {
            return userDao.update(user);
        } catch (DaoException e) {
            throw new ServiceException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteUser(long id) {
        try {
            return userDao.delete(id);
        } catch (DaoException e) {
            throw new ServiceException("Failed to delete user: " + e.getMessage(), e);
        }
    }
}
