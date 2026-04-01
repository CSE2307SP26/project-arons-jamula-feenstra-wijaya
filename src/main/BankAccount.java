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

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public void deposit(double amount) {
        deposit(amount, true);
    }
    
    public void deposit(double amount, boolean recordTransaction) {
        amount = roundToTwoDecimals(amount);
        if(amount > 0) {
            this.balance += amount;
            if(recordTransaction) {
                this.transactionHistory.add(String.format("Deposit: $%.2f", amount));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void withdraw(double amount) {
        withdraw(amount, true);
    }

    public void withdraw(double amount, boolean recordTransaction) {
        amount = roundToTwoDecimals(amount);
        if (amount <= 0) {  
            throw new IllegalArgumentException();          
        } else if (amount > this.balance) {
            throw new IllegalArgumentException();
        } else {
            this.balance -= amount;
            if(recordTransaction) {
                this.transactionHistory.add(String.format("Withdraw: $%.2f", amount));  
            }
        }
    }

    public void transfer(BankAccount otherBankAccount, double amount) {
        amount = roundToTwoDecimals(amount);
        if (amount <= 0) {
            throw new IllegalArgumentException();
        } else if (amount > this.balance) {
            throw new IllegalArgumentException();
        } else {
            this.withdraw(amount, false);
            this.transactionHistory.add(String.format("Transferred: $%.2f to %s", amount, otherBankAccount.getName()));
            otherBankAccount.deposit(amount, false);
            otherBankAccount.getHistory().add(String.format("Received: $%.2f from %s", amount, this.getName()));
        }
    }

    public void collectFees(double amount) {
        collectFees(amount, true);
    }

    public void collectFees(double amount, boolean recordTransaction) {
        amount = roundToTwoDecimals(amount);
        if (amount <= 0) {
            throw new IllegalArgumentException();                
        } else {
            this.balance -= amount;
            if(recordTransaction) {
                this.transactionHistory.add(String.format("Fee Collected: $%.2f", amount));
            }
        }
    }
   
    public void applyInterest(double interestRate) {
        applyInterest(interestRate, true);
    }

    public void applyInterest(double interestRate, boolean recordTransaction) {
        if (interestRate <= 0 || this.balance < 0) {
            throw new IllegalArgumentException();
        } else {
            double rawInterest = this.balance * interestRate;
            double roundedInterest = roundToTwoDecimals(rawInterest);
            this.balance += roundedInterest;
            if(recordTransaction) {
                this.transactionHistory.add(String.format("Interest Applied: $%.2f", roundedInterest));
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
