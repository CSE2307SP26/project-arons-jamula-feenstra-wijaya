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

    public void checkBalance() {
        if (this.balance  >= 0) {
            System.out.println("This account has a balance of $" + this.balance);
        } else if (this.balance < 0) {
            System.out.println("This account has a balance of -$" + this.balance);
        }
    }

    public double getBalance() {
        return this.balance;
    }

    public String getName() {
        return this.name;
    }
}
