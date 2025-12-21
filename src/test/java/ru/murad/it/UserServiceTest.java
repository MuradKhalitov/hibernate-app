package ru.murad.it;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.murad.dao.UserDao;
import ru.murad.exception.ServiceException;
import ru.murad.model.User;
import ru.murad.service.UserService;
import ru.murad.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends AbstractIntegrationTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        cleanDatabase();

        UserDao userDao = new UserDao(sessionFactory);
        userService = new UserServiceImpl(userDao);
    }

    private void cleanDatabase() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.createMutationQuery("delete from User").executeUpdate();

            session.getTransaction().commit();
        }
    }


    @Test
    void createUser_shouldPersistUser() {
        User user = userService.createUser("Murad", "murad@test.com", 25);

        assertNotNull(user.getId());
        assertEquals("Murad", user.getName());
    }

    @Test
    void createUser_shouldPersistMultipleUsers() {
        User u1 = userService.createUser("A", "a@test.com", 20);
        User u2 = userService.createUser("B", "b@test.com", 25);

        assertNotEquals(u1.getId(), u2.getId());

        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }


    @Test
    void getUserById_shouldReturnUser() {
        User saved = userService.createUser("Test", "test@test.com", 30);

        Optional<User> result = userService.getUserById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("Test", result.get().getName());
    }

    @Test
    void getUserById_shouldReturnEmpty_whenUserNotExists() {
        Optional<User> result = userService.getUserById(999L);

        assertTrue(result.isEmpty());
    }


    @Test
    void getAllUsers_shouldReturnAllUsers() {
        userService.createUser("User1", "u1@test.com", 20);
        userService.createUser("User2", "u2@test.com", 22);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void updateUser_shouldUpdateFields() {
        User saved = userService.createUser("Old", "old@test.com", 40);

        User updated = userService.updateUser(
                saved.getId(),
                "New",
                "new@test.com",
                35
        );

        assertEquals("New", updated.getName());
        assertEquals("new@test.com", updated.getEmail());
        assertEquals(35, updated.getAge());
    }

    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {
        ServiceException ex = assertThrows(
                ServiceException.class,
                () -> userService.updateUser(999L, "Name", "email@test.com", 30)
        );

        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void updateUser_shouldPersistChangesInDatabase() {
        User saved = userService.createUser("Old", "old@test.com", 40);

        userService.updateUser(saved.getId(), "New", "new@test.com", 35);

        User reloaded = userService.getUserById(saved.getId()).orElseThrow();

        assertEquals("New", reloaded.getName());
        assertEquals("new@test.com", reloaded.getEmail());
        assertEquals(35, reloaded.getAge());
    }



    @Test
    void deleteUser_shouldRemoveUser() {
        User saved = userService.createUser("ToDelete", "del@test.com", 50);

        boolean deleted = userService.deleteUser(saved.getId());

        assertTrue(deleted);
        assertTrue(userService.getUserById(saved.getId()).isEmpty());
    }

    @Test
    void deleteUser_shouldReturnFalse_whenUserNotFound() {
        boolean result = userService.deleteUser(999L);

        assertFalse(result);
    }

    @Test
    void deleteUser_shouldBeIdempotent() {
        User saved = userService.createUser("ToDelete", "del@test.com", 50);

        assertTrue(userService.deleteUser(saved.getId()));
        assertFalse(userService.deleteUser(saved.getId()));
    }


}
