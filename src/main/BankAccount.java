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

    public double getBalance() {
        return this.balance;
    }

    public String getName() {
        return this.name;
    }
}
