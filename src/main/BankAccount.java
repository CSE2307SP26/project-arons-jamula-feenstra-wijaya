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

    public void transfer(BankAccount otherBankAccount, double amount) {
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

    public void collectFees(double amount) {
        collectFees(amount, true);
    }

    public void collectFees(double amount, boolean recordTransaction) {
        if (amount <= 0) {
            throw new IllegalArgumentException();                
        } else {
            this.balance -= amount;
            if(recordTransaction) {
                this.transactionHistory.add("Fee Collected: $" + amount);
            }
        }
    }
   
    public void applyInterest(double amount) {
        applyInterest(amount, true);
    }

    public void applyInterest(double interestRate, boolean recordTransaction) {
        if (interestRate <= 0 || this.balance < 0) {
            throw new IllegalArgumentException();
        } else {
            double oldBalance = this.balance;
            this.balance *= (1 + interestRate);
             if(recordTransaction) {
                this.transactionHistory.add("Interest Applied: $" + (oldBalance * (1 + interestRate) - oldBalance));
            }
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
