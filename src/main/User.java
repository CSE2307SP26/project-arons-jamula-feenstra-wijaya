package main;

import java.util.HashMap;

public class User {

    /*--------------------------------------------------------
                            Constants
    ---------------------------------------------------------*/
    private static final int MAX_FAILED_ATTEMPTS = 3;

    /*--------------------------------------------------------
                            Fields
    ---------------------------------------------------------*/
    private String username;
    private String password;
    private HashMap<String, BankAccount> accounts;
    private boolean isLocked;
    private int failedAttempts;

    /*--------------------------------------------------------
                          Constructor
    ---------------------------------------------------------*/
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.accounts = new HashMap<>();
        this.isLocked = false;
        this.failedAttempts = 0;
    }

    /*--------------------------------------------------------
                            Core Actions
    ---------------------------------------------------------*/
    public boolean login(String inputPassword) {
        if (isLocked) return false;

        if (password.equals(inputPassword)) {
            failedAttempts = 0;
            return true;
        }

        failedAttempts++;
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            isLocked = true;
        }
        return false;
    }

    public void unlockAccount() {
        failedAttempts = 0;
        isLocked = false;
    }

    public void changePassword(String newPassword) {
        password = newPassword;
    }

    public void addAccount(BankAccount account) {
        accounts.put(account.getName(), account);
    }

    /*--------------------------------------------------------
                         Getters / Accessors
    ---------------------------------------------------------*/
    public String getUsername() {
        return username;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public BankAccount getAccount(String accountName) {
        if (!accounts.containsKey(accountName)) {
            throw new IllegalArgumentException("Account not found: " + accountName);
        }
        return accounts.get(accountName);
    }

    public HashMap<String, BankAccount> getAllAccounts() {
        return accounts;
    }
}