package test;

import main.BankAccount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

public class BankAccountTest {

    @Test
    public void testDeposit() {
        BankAccount testAccount = new BankAccount("test");
        testAccount.deposit(50);
        assertEquals(50, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testInvalidDeposit() {
        BankAccount testAccount = new BankAccount("test");
        try {
            testAccount.deposit(-50);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }
    
    // because checkBalance() is essentially a getter method as it just 
    // prints the balance, I am not sure a test is appropriate

    @Test
    public void testWithdraw() {
        BankAccount testAccount = new BankAccount("test");
        testAccount.deposit(100);
        testAccount.withdraw(40);
        assertEquals(60, testAccount.getBalance(), 0.01);
    }
  
    @Test
    public void testWithdrawTooMuch() {
        BankAccount testAccount = new BankAccount("test");
        testAccount.deposit(50);
        try {
            testAccount.withdraw(100);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }

    @Test
    public void testInvalidWithdraw() {
        BankAccount testAccount = new BankAccount("test");
        try {
            testAccount.withdraw(-10);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }


}