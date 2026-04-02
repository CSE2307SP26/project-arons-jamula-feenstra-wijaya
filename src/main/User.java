package main;

import java.util.HashMap;

public class User {
    private String username;
    private String password;
    private HashMap<String, BankAccount> accounts;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.accounts = new HashMap<>();
    }

    public boolean login(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public String getUsername() {
        return this.username;
    }

    public void addAccount(BankAccount account) {
        this.accounts.put(account.getName(), account);
    }

    public BankAccount getAccount(String accountName) {
        if(!accounts.containsKey(accountName)) {
            throw new IllegalArgumentException("Account not found: " + accountName);
        }
        return this.accounts.get(accountName);
    }

    public HashMap<String, BankAccount> getAllAccounts() {
        return this.accounts;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
}
