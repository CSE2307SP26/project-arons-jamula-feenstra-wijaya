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
        deposit(amount, true);
    }
    
    public void deposit(double amount, boolean recordTransaction) {
        if(amount > 0) {
            this.balance += amount;
            if(recordTransaction) {
                this.transactionHistory.add("Deposit: $" + amount);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void withdraw(double amount) {
        withdraw(amount, true);
    }

    public void withdraw(double amount, boolean recordTransaction) {
        if (amount <= 0) {
            throw new IllegalArgumentException();
        } else if (amount > this.balance) {
            throw new IllegalArgumentException();
        } else {
            this.balance -= amount;
            if(recordTransaction) {
                this.transactionHistory.add("Withdraw: $" + amount);
            }
        }
    }

    public void transfer(BankAccount otherBankAccount, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException();
        } else if (amount > this.balance) {
            throw new IllegalArgumentException();
        } else {
            this.withdraw(amount, false);
            this.transactionHistory.add("Transferred: $" + amount + " to " + otherBankAccount.getName());
            otherBankAccount.deposit(amount, false);
            otherBankAccount.getHistory().add("Received: $" + amount + " from " + this.getName());
        }
    }

    public void applyInterest(double interestRate) {
        if (interestRate <= 0 || this.balance < 0) {
            throw new IllegalArgumentException();
        } else {
            this.balance *= (1 + interestRate);
        }
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double newBalance) {
        this.balance = newBalance; //only used for test cases, allows for negative values
    }

    public String getName() {
        return this.name;
    }

    public LinkedList<String> getHistory() {
        return this.transactionHistory;
    }
}
