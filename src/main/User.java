package main;

import java.util.HashMap;

public class User {
    private String username;
    private String password;
    private HashMap<String, BankAccount> accounts;
    private boolean isLocked;
    private int failedAttempts;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.accounts = new HashMap<>();
        this.isLocked = false;
        this.failedAttempts = 0;
    }

    public boolean login(String inputPassword) {
        if (this.isLocked) {
            return false;
        }
        if (this.password.equals(inputPassword)) {
            this.failedAttempts = 0;
            return true;
        } else {
            this.failedAttempts++;
            if (this.failedAttempts >= MAX_FAILED_ATTEMPTS) {
                this.isLocked = true;
            }
            return false;
        }
    }

    public void unlockAccount() {
        this.failedAttempts = 0;
        this.isLocked = false;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isLocked() {
        return this.isLocked;
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
