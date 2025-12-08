package ru.murad.service;

import ru.murad.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(String name, String email, int age);

    Optional<User> getUserById(long id);

    List<User> getAllUsers();

    User updateUser(long id, String name, String email, int age);

    boolean deleteUser(long id);
}

