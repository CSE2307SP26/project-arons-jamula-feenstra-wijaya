package main;

public class BankAccount {

    private double balance;
    private String name;

    public BankAccount(String name) {
        this.balance = 0;
        this.name = name;
    }

    public void deposit(double amount) {
        if(amount > 0) {
            this.balance += amount;
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

    public void applyInterest(double interestRate) {
        if (interestRate < 0 || this.balance < 0) {
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
}
