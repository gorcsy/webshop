package com.codecool.buyourstuff.dao.implementation.db;

import com.codecool.buyourstuff.dao.DataManager;
import com.codecool.buyourstuff.dao.UserDao;
import com.codecool.buyourstuff.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoDbTest {
    private static final UserDao userDao = DataManager.getUserDao();
    private User testUser;

    @BeforeEach
    public void setup() {
        clearTable();
        testUser = new User("test user", "1234");
    }

    @Test
    public void TestAdd() {
        userDao.add(testUser);

        assertNotEquals(0, testUser.getId());
    }

    @Test
    public void TestAddNull() {

        assertThrows(NullPointerException.class, () -> userDao.add(null));
    }

    @Test
    public void TestFindExistingUser() {

        userDao.add(testUser);

        User foundUser = userDao.find("test user", "1234");

        assertEquals("test user", foundUser.getName());
    }

    @Test
    public void TestFindNotExistingUser() {
        userDao.add(testUser);
        String exceptionMessage = "";

        try {
            userDao.find("test user 2", "1234");
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }

        assertEquals("Wrong username", exceptionMessage);
    }

    @Test
    public void TestFindExistingUserWrongPassword() {
        userDao.add(testUser);
        String exceptionMessage = "";

        try {
            userDao.find("test user", "5678");
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }

        assertEquals("Wrong password", exceptionMessage);
    }

    @Test
    public void TestClear() {
        userDao.add(testUser);
        String exceptionMessage = "";

        userDao.clear();
        try {
            userDao.find("test user", "1234");
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
        }

        assertEquals("Wrong username", exceptionMessage);
    }

    @Test
    public void TestIsNameAvailableNameAvailable() {
        userDao.add(testUser);

        assertTrue(userDao.isNameAvailable("test user 2"));
    }

    @Test
    public void TestIsNameAvailableNameUnavailable() {
        userDao.add(testUser);

        assertFalse(userDao.isNameAvailable("test user"));
    }

    private void clearTable() {
        userDao.clear();
    }
}
