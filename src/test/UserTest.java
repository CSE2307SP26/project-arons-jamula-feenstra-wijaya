package test;

import main.User;
import main.BankAccount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    public void testLogin() {
        User testUser = new User("test", "password");
        assertEquals(true, testUser.login("password"));
        assertEquals(false, testUser.login("wrongpassword"));
    }

    @Test
    public void testLoginLockout() {
        User testUser = new User("test", "correctpassword");
        testUser.login("wrongpassword1");
        testUser.login("wrongpassword2");
        testUser.login("wrongpassword3");
        assertEquals(false, testUser.login("correctpassword"));
        testUser.unlockAccount();
        assertEquals(true, testUser.login("correctpassword"));
    }

    @Test
    public void testAddAndGetAccount() {
        User testUser = new User("test", "password");
        BankAccount testAccount = new BankAccount("test account");
        testUser.addAccount(testAccount);
        assertEquals(testAccount, testUser.getAccount("test account"));
    }

    @Test
    public void testGetNonexistentAccount() {
        User testUser = new User("test", "password");
        try {
            testUser.getAccount("nonexistent account");
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }

    @Test
    public void testChangePassword() {
        User testUser = new User("test", "password");
        testUser.changePassword("newpassword");
        assertEquals(true, testUser.login("newpassword"));
        assertEquals(false, testUser.login("password"));
    }
}
