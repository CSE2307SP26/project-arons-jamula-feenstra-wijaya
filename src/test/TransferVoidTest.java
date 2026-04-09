package test;

import main.AdminMenu;
import main.BankAccount;
import main.Transaction;
import main.User;

import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

public class TransferVoidTest {
    private User makeUser(String username, double balance) {
        User user = new User(username, "pass");
        BankAccount account = new BankAccount(username);
        account.setBalance(balance);
        user.addAccount(account);
        return user;
    }

    private HashMap<String, User> makeDatabase(User[] users) {
        HashMap<String, User> db = new HashMap<>();
        for (User u : users) db.put(u.getUsername(), u);
        return db;
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
        User alice = makeUser("alice", 100);
        User bob   = makeUser("bob",   0);
        BankAccount aliceAcct = alice.getAllAccounts().get("alice");
        BankAccount bobAcct   = bob.getAllAccounts().get("bob");

        aliceAcct.transferBetweenUsers(bobAcct, 40, "alice", "bob");

        assertEquals(60, aliceAcct.getBalance(), 0.01);
        assertEquals(40, bobAcct.getBalance(), 0.01);

        System.setIn(new ByteArrayInputStream("alice\nalice\n1\n".getBytes()));
        new AdminMenu(makeDatabase(alice, bob)).processInput(4);

        assertEquals(100, aliceAcct.getBalance(), 0.01);
        assertEquals(0,   bobAcct.getBalance(),   0.01);
    }

    @Test
    public void testVoidReplacesHistoryEntries() {
        User alice = makeUser("alice", 100);
        User bob   = makeUser("bob",   0);
        BankAccount aliceAcct = alice.getAllAccounts().get("alice");
        BankAccount bobAcct   = bob.getAllAccounts().get("bob");
        aliceAcct.transferBetweenUsers(bobAcct, 40, "alice", "bob");
        
        System.setIn(new ByteArrayInputStream("alice\nalice\n1\n".getBytes()));
        new AdminMenu(makeDatabase(alice, bob)).processInput(4);

        assertEquals(1, aliceAcct.getHistory().size());
        assertEquals("void", aliceAcct.getHistory().get(0).getType());
        assertEquals(1, bobAcct.getHistory().size());
        assertEquals("void", bobAcct.getHistory().get(0).getType());
    }
}
