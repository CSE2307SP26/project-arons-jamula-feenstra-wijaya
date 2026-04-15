package test;

import main.BankAccount;
import main.Transaction;

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
    public void testCollectFees() {
        BankAccount testAccount = new BankAccount("test");
        testAccount.setBalance(100);
        testAccount.collectFees(20);
        assertEquals(80, testAccount.getBalance(), 0.01);

    }

    @Test
    public void testCollectFeesOverdrawn() {
        BankAccount testAccount = new BankAccount("test");
        testAccount.setBalance(20);
        testAccount.collectFees(30);
        assertEquals(-10, testAccount.getBalance(), 0.01);

    }

    @Test
    public void testCollectFeesFail() {
        BankAccount testAccount = new BankAccount("test");
        testAccount.setBalance(20);
        try {
            testAccount.collectFees(-20);
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

    @Test
    public void testTransactionHistory() {
        BankAccount testAccount = new BankAccount("test");
        testAccount.deposit(100);
        testAccount.withdraw(30);
        testAccount.deposit(50);
        assertEquals(3, testAccount.getHistory().size());
        assertEquals("Deposit: $100.00", testAccount.getHistory().get(0).getDescription());
        assertEquals("Withdraw: $30.00", testAccount.getHistory().get(1).getDescription());
        assertEquals("Deposit: $50.00", testAccount.getHistory().get(2).getDescription());
    }

    @Test
    public void testTransferBetweenUsersBalances() {
        BankAccount sender = new BankAccount("sender");
        BankAccount recipient = new BankAccount("recipient");
        sender.setBalance(100);
        sender.transferBetweenUsers(recipient, 40, "alice", "bob");

        assertEquals(60, sender.getBalance(), 0.01);
        assertEquals(40, recipient.getBalance(), 0.01);
    }

    @Test
    public void testTransferBetweenUsersRecordsHistory() {
        BankAccount sender = new BankAccount("sender");
        BankAccount recipient = new BankAccount("recipient");
        sender.setBalance(100);
        sender.transferBetweenUsers(recipient, 40, "alice", "bob");

        assertEquals(1, sender.getHistory().size());
        assertEquals(1, recipient.getHistory().size());
        assertEquals("inter-user-transfer", sender.getHistory().get(0).getType());
        assertEquals("inter-user-receipt", recipient.getHistory().get(0).getType());
    }

    @Test
    public void testVoidRestoresBalances() {
        BankAccount aliceAcct = new BankAccount("alice");
        BankAccount bobAcct = new BankAccount("bob");
        aliceAcct.setBalance(100);
        aliceAcct.transferBetweenUsers(bobAcct, 40, "alice", "bob");

        Transaction senderTx = aliceAcct.getHistory().get(0);
        aliceAcct.reverseTransfer(bobAcct, "alice", senderTx);

        assertEquals(100, aliceAcct.getBalance(), 0.01);
        assertEquals(0, bobAcct.getBalance(), 0.01);
    }

    @Test
    public void testVoidReplacesHistoryEntries() {
        BankAccount aliceAcct = new BankAccount("alice");
        BankAccount bobAcct = new BankAccount("bob");
        aliceAcct.setBalance(100);
        aliceAcct.transferBetweenUsers(bobAcct, 40, "alice", "bob");

        Transaction senderTx = aliceAcct.getHistory().get(0);
        aliceAcct.reverseTransfer(bobAcct, "alice", senderTx);

        assertEquals(1, aliceAcct.getHistory().size());
        assertEquals("void", aliceAcct.getHistory().get(0).getType());
        assertEquals(1, bobAcct.getHistory().size());
        assertEquals("void", bobAcct.getHistory().get(0).getType());
    }
}