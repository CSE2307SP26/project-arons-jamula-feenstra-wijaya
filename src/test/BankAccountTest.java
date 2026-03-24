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

    @Test
    public void testWithdraw() {
        BankAccount testAccount = new BankAccount("test");
        testAccount.setBalance(100);
        testAccount.withdraw(40);
        assertEquals(60, testAccount.getBalance(), 0.01);
    }
  
    @Test
    public void testWithdrawTooMuch() {
        BankAccount testAccount = new BankAccount("test");
        testAccount.setBalance(50);
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
        testAccount.setBalance(50);
        try {
            testAccount.withdraw(-10);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }

    @Test
    public void testTransfer() {
        BankAccount testAccount1 = new BankAccount("test1");
        BankAccount testAccount2 = new BankAccount("test2");
        testAccount1.setBalance(100);
        testAccount1.transfer(testAccount2, 40);
        assertEquals(60, testAccount1.getBalance(), 0.01);
        assertEquals(40, testAccount2.getBalance(), 0.01);
    }

    @Test
    public void testInvalidTransfer() {
        BankAccount testAccount1 = new BankAccount("test1");
        BankAccount testAccount2 = new BankAccount("test2");
        testAccount1.setBalance(100);
        try {
            testAccount1.transfer(testAccount2, -10);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }

    @Test
    public void testTransferTooMuch() {
        BankAccount testAccount1 = new BankAccount("test1");
        BankAccount testAccount2 = new BankAccount("test2");
        testAccount1.setBalance(50);
        try {
            testAccount1.transfer(testAccount2, 100);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }

    @Test
    public void testApplyInterest() {
        BankAccount testAccount = new BankAccount("test");
        testAccount.setBalance(50);
        testAccount.applyInterest(0.05);
        assertEquals(52.5, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testApplyInterestNegativeBalance() {
        BankAccount testAccount = new BankAccount("test");
        testAccount.setBalance(-50);
        try {
            testAccount.applyInterest(0.05);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }
    
    @Test
    public void testApplyInterestNegativeInterestRate() {
        BankAccount testAccount = new BankAccount("test");
        testAccount.setBalance(50);
        try {
            testAccount.applyInterest(-0.05);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }
}