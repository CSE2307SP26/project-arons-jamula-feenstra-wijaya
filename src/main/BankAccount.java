package main;

import java.util.LinkedList;

public class BankAccount {

    private double balance;
    private String name;
    private LinkedList<String> transactionHistory;

    public BankAccount(String name) {
        this.balance = 0;
        this.name = name;
        this.transactionHistory = new LinkedList<>();
    }

    public void deposit(double amount) {
        if(amount > 0) {
            this.balance += amount;
            this.transactionHistory.add("Deposit: $" + amount);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException();
        } else if (amount > this.balance) {
            throw new IllegalArgumentException();
        } else {
            this.balance -= amount;
        }
    }

    public void transfer(BankAccount otherBankAccount, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException();
        } else if (amount > this.balance) {
            throw new IllegalArgumentException();
        } else {
            this.withdraw(amount);
            otherBankAccount.deposit(amount);
        }
    }

    public double getBalance() {
        return this.balance;
    }

    public String getName() {
        return this.name;
    }

    public LinkedList<String> getHistory() {
        return this.transactionHistory;
    }
}
