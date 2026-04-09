package main;

import java.util.LinkedList;

public class BankAccount {

    private double balance;
    private String name;
    private LinkedList<Transaction> transactionHistory;

    public BankAccount(String name) {
        this.balance = 0;
        this.name = name;
        this.transactionHistory = new LinkedList<Transaction>();
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
                this.transactionHistory.add(new Transaction("deposit",
                        String.format("Deposit: $%.2f", amount), amount));
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
                this.transactionHistory.add(new Transaction("withdraw",
                        String.format("Withdraw: $%.2f", amount), amount));
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
            this.transactionHistory.add(new Transaction("transfer",
                    String.format("Transferred: $%.2f to %s", amount, otherBankAccount.getName()),
                    amount, null, otherBankAccount.getName()));
            otherBankAccount.deposit(amount, false);
            otherBankAccount.getHistory().add(new Transaction("received",
                    String.format("Received: $%.2f from %s", amount, this.getName()),
                    amount, null, this.getName()));
        }
    }

    public void transferBetweenUsers(BankAccount otherBankAccount, double amount,
            String fromUserUsername, String toUserUsername) {
        amount = roundToTwoDecimals(amount);
        if (amount <= 0) {
            throw new IllegalArgumentException();
        } else if (amount > this.balance) {
            throw new IllegalArgumentException();
        } else {
            this.withdraw(amount, false);

            // Create both transactions first, then link them to each other by ID.
            Transaction senderTransaction = new Transaction("inter-user-transfer",
                    String.format("Inter-user transfer: $%.2f to %s with account name %s",
                            amount, toUserUsername, otherBankAccount.getName()),
                    amount, toUserUsername, otherBankAccount.getName());

            Transaction recipientTransaction = new Transaction("inter-user-receipt",
                    String.format("Inter-user transfer: $%.2f from %s with account name %s",
                            amount, fromUserUsername, this.getName()),
                    amount, fromUserUsername, this.getName());

            senderTransaction.setLinkedId(recipientTransaction.getId());
            recipientTransaction.setLinkedId(senderTransaction.getId());

            this.transactionHistory.add(senderTransaction);
            otherBankAccount.deposit(amount, false);
            otherBankAccount.getHistory().add(recipientTransaction);
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
                this.transactionHistory.add(new Transaction("fee",
                        String.format("Fee Collected: $%.2f", amount), amount));
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
                this.transactionHistory.add(new Transaction("interest",
                        String.format("Interest Applied: $%.2f", roundedInterest), roundedInterest));
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

    public LinkedList<Transaction> getHistory() {
        return this.transactionHistory;
    }
}
