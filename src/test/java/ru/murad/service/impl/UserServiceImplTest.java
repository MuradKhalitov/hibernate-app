package ru.murad.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.murad.dao.UserDao;
import ru.murad.exception.DaoException;
import ru.murad.exception.ServiceException;
import ru.murad.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_success() {
        User saved = new User();
        saved.setId(1L);
        saved.setName("Murad");

        when(userDao.save(any(User.class))).thenReturn(saved);

        User result = userService.createUser("Murad", "murad@test.com", 25);

        assertEquals(1L, result.getId());
        verify(userDao).save(any(User.class));
    }

    @Test
    void createUser_daoException_shouldThrowServiceException() {
        when(userDao.save(any(User.class)))
                .thenThrow(new DaoException("db error"));

        ServiceException ex = assertThrows(
                ServiceException.class,
                () -> userService.createUser("Murad", "murad@test.com", 25)
        );

        assertTrue(ex.getMessage().contains("Failed to create user"));
        verify(userDao).save(any(User.class));
    }

    @Test
    void getUserById_found() {
        User user = new User();
        user.setId(1L);

        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        verify(userDao).findById(1L);
    }

    @Test
    void getUserById_notFound() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isEmpty());
        verify(userDao).findById(1L);
    }

    @Test
    void getAllUsers_success() {
        when(userDao.findAll()).thenReturn(List.of(new User(), new User()));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userDao).findAll();
    }

    @Test
    void updateUser_success() {
        User existing = new User();
        existing.setId(1L);

        when(userDao.findById(1L)).thenReturn(Optional.of(existing));
        when(userDao.update(any(User.class))).thenReturn(existing);

        User result = userService.updateUser(1L, "New", "new@test.com", 30);

        assertEquals("New", result.getName());
        verify(userDao).findById(1L);
        verify(userDao).update(existing);
    }

    @Test
    void updateUser_notFound_shouldThrowServiceException() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(
                ServiceException.class,
                () -> userService.updateUser(1L, "New", "new@test.com", 30)
        );

        assertTrue(ex.getMessage().contains("not found"));
        verify(userDao).findById(1L);
        verify(userDao, never()).update(any());
    }

    @Test
    void updateUser_daoException_shouldThrowServiceException() {
        User existing = new User();
        existing.setId(1L);

        when(userDao.findById(1L)).thenReturn(Optional.of(existing));
        when(userDao.update(any(User.class)))
                .thenThrow(new DaoException("db error"));

        assertThrows(
                ServiceException.class,
                () -> userService.updateUser(1L, "New", "new@test.com", 30)
        );

        verify(userDao).update(existing);
    }

    @Test
    void deleteUser_success() {
        when(userDao.delete(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userDao).delete(1L);
    }

    @Test
    void deleteUser_daoException_shouldThrowServiceException() {
        when(userDao.delete(1L))
                .thenThrow(new DaoException("db error"));

        assertThrows(
                ServiceException.class,
                () -> userService.deleteUser(1L)
        );

        verify(userDao).delete(1L);
    }
}

