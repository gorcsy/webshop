package com.codecool.buyourstuff.dao.implementation.file;

import com.codecool.buyourstuff.model.User;
import com.codecool.buyourstuff.model.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoFileTest {

    private final UserDaoFile underTest = new UserDaoFile("user_test.json");
    private User user;

    @BeforeEach
    void setup() {
        underTest.clear();
        user = new User("cica", "kiscica");
    }

    @Test
    public void shouldAddUser() {
        underTest.add(user);

        assertNotEquals(0, user.getId());
    }

    @Test
    public void shouldFindUser() {

        underTest.add(user);

        User actual = underTest.find("cica", "kiscica");

        assertEquals(user, actual);
    }

    @Test
    public void shouldThrowDataNotFoundException_whenUserNotFound() {

        Executable action = () -> underTest.find("kiscica", "cica");

        assertThrows(DataNotFoundException.class, action);
    }

    @Test
    public void shouldThrowDataNotFoundException_whenWrongPassword() {

        underTest.add(user);

        Executable action = () -> underTest.find("cica", "kiskutya");

        assertThrows(DataNotFoundException.class, action);
    }

    @Test
    public void shouldClearList() {

        underTest.add(user);

        underTest.clear();

        assertEquals(0, underTest.getAll().size());
    }
}
