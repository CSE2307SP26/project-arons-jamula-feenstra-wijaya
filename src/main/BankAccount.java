package main;

import java.util.LinkedList;

public class BankAccount {

    /*--------------------------------------------------------
                            Fields
    ---------------------------------------------------------*/
    private double balance;
    private String name;
    private LinkedList<Transaction> transactionHistory;
    private String accountType;
    private double warningThreshold;
    private boolean isFrozen;

    /*--------------------------------------------------------
                          Constructors
    ---------------------------------------------------------*/
    public BankAccount(String name, String accountType) {
        this.balance = 0;
        this.name = name;
        this.transactionHistory = new LinkedList<>();
        this.accountType = accountType;
        this.isFrozen = false;
    }

    public BankAccount(String name) {
        this(name, "Savings"); //accounts are savings accounts by default
    }

    /*--------------------------------------------------------
                         Core Actions
    ---------------------------------------------------------*/
    public void deposit(double amount) {
        deposit(amount, true);
    }

    public void deposit(double amount, String note) {
        amount = roundToTwoDecimals(amount);
        if(amount > 0) {
            this.balance += amount;
            this.transactionHistory.add(new Transaction("deposit",
                    String.format("Deposit: $%.2f", amount), amount, null, null, note));
        } else {
            throw new IllegalArgumentException();
        }
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

    public void withdraw(double amount, String note) {
        amount = roundToTwoDecimals(amount);
        if (amount <= 0) {
            throw new IllegalArgumentException();
        } else if (amount > this.balance) {
            throw new IllegalArgumentException();
        } else {
            this.balance -= amount;
            this.transactionHistory.add(new Transaction("withdraw",
                    String.format("Withdraw: $%.2f", amount), amount, null, null, note));
        }
    }

    public void withdraw(double amount, boolean recordTransaction) {
        amount = roundToTwoDecimals(amount);
        if (amount <= 0 || amount > this.balance) {
            throw new IllegalArgumentException();
        }

        this.balance -= amount;
        if(recordTransaction) {
            this.transactionHistory.add(new Transaction("withdraw",
                    String.format("Withdraw: $%.2f", amount), amount));
        }
    }

    public void transfer(BankAccount otherBankAccount, double amount) {
        transfer(otherBankAccount, amount, null);
    }

    public void transfer(BankAccount otherBankAccount, double amount, String note) {
        amount = roundToTwoDecimals(amount);
        validateTransfer(amount);

        this.withdraw(amount, false);

        this.transactionHistory.add(new Transaction("transfer",
                String.format("Transferred: $%.2f to %s", amount, otherBankAccount.getName()),
                amount, null, otherBankAccount.getName(), note));

        otherBankAccount.deposit(amount, false);
        otherBankAccount.getHistory().add(new Transaction("received",
                String.format("Received: $%.2f from %s", amount, this.getName()),
                amount, null, this.getName(), null));
    }

    public void transferBetweenUsers(BankAccount otherBankAccount, double amount,
            String fromUserUsername, String toUserUsername) {
        transferBetweenUsers(otherBankAccount, amount, fromUserUsername, toUserUsername, null);
    }

    public void transferBetweenUsers(BankAccount otherBankAccount, double amount,
            String fromUserUsername, String toUserUsername, String note) {

        amount = roundToTwoDecimals(amount);
        validateTransfer(amount);

        this.withdraw(amount, false);

        Transaction[] transactions = createInterUserTransactions(
                otherBankAccount, amount, fromUserUsername, toUserUsername, note);

        applyInterUserTransfer(otherBankAccount, amount, transactions);
    }

    public void collectFees(double amount) {
        collectFees(amount, true);
    }

    public void collectFees(double amount, boolean recordTransaction) {
        amount = roundToTwoDecimals(amount);
        if (amount <= 0) {
            throw new IllegalArgumentException();                
        }

        this.balance -= amount;
        if(recordTransaction) {
            this.transactionHistory.add(new Transaction("fee",
                    String.format("Fee Collected: $%.2f", amount), amount));
        }
    }

    public void applyInterest(double interestRate) {
        applyInterest(interestRate, true);
    }

    public void applyInterest(double interestRate, boolean recordTransaction) {
        if (interestRate <= 0 || this.balance < 0) {
            throw new IllegalArgumentException();
        }

        double rawInterest = this.balance * interestRate;
        double roundedInterest = roundToTwoDecimals(rawInterest);
        this.balance += roundedInterest;

        if(recordTransaction) {
            this.transactionHistory.add(new Transaction("interest",
                    String.format("Interest Applied: $%.2f", roundedInterest), roundedInterest));
        }
    }

    public void undoTransaction(Transaction tx) {
        if (tx.getType().equals("deposit") || tx.getType().equals("interest")) {
            this.balance -= tx.getAmount();
        }
        if (tx.getType().equals("withdraw") || tx.getType().equals("fee")) {
            this.balance += tx.getAmount();
        }
        this.transactionHistory.add(new Transaction("undo", 
                String.format("UNDO (admin): Reversed %s of %.2f", 
                        tx.getType(), 
                        tx.getAmount()), 
                tx.getAmount()));
    }

    public void reverseTransfer(BankAccount recipientAcct, String senderUsername, Transaction senderTx) {
        Transaction recipientTx = findTransactionById(recipientAcct.getHistory(), senderTx.getLinkedId());
        recipientAcct.withdraw(senderTx.getAmount(), false);
        this.deposit(senderTx.getAmount(), false);
        this.transactionHistory.remove(senderTx);
        if (recipientTx != null) recipientAcct.getHistory().remove(recipientTx);
        this.transactionHistory.add(new Transaction("void",
                String.format("VOID (admin): Reversed transfer of $%.2f to %s (%s)",
                        senderTx.getAmount(),
                        senderTx.getRelatedUser(),
                        senderTx.getRelatedAccount()),
                senderTx.getAmount()));
        recipientAcct.getHistory().add(new Transaction("void",
                String.format("VOID (admin): Reversed transfer of $%.2f from %s (%s)",
                        senderTx.getAmount(),
                        senderUsername,
                        this.getName()),
                senderTx.getAmount()));
    }

    /*--------------------------------------------------------
                         Getters / Setters
    ---------------------------------------------------------*/
    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double newBalance) {
        this.balance = newBalance;
    }

    public String getName() {
        return this.name;
    }

    public LinkedList<Transaction> getHistory() {
        return this.transactionHistory;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public double getWarningThreshold() {
        return this.warningThreshold;
    }

    public void setWarningThreshold(double amount) {
        amount = roundToTwoDecimals(amount);
        this.warningThreshold = amount;
    }

    public boolean getIsFrozen() {
        return this.isFrozen;
    }

    public void setIsFrozen(boolean isFrozen) {
        this.isFrozen = isFrozen;
    }


    /*--------------------------------------------------------
                         Helper Methods
    ---------------------------------------------------------*/
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private void validateTransfer(double amount) {
        if (amount <= 0 || amount > this.balance) {
            throw new IllegalArgumentException();
        }
    }

    private Transaction[] createInterUserTransactions(BankAccount otherBankAccount, double amount,
            String fromUser, String toUser, String note) {

        Transaction senderTransaction = new Transaction("inter-user-transfer",
                String.format("Inter-user transfer: $%.2f to %s with account name %s",
                        amount, toUser, otherBankAccount.getName()),
                amount, toUser, otherBankAccount.getName(), note);

        Transaction recipientTransaction = new Transaction("inter-user-receipt",
                String.format("Inter-user transfer: $%.2f from %s with account name %s",
                        amount, fromUser, this.getName()),
                amount, fromUser, this.getName(), null);

        senderTransaction.setLinkedId(recipientTransaction.getId());
        recipientTransaction.setLinkedId(senderTransaction.getId());

        return new Transaction[]{senderTransaction, recipientTransaction};
    }

    private void applyInterUserTransfer(BankAccount otherBankAccount, double amount,
            Transaction[] transactions) {

        this.transactionHistory.add(transactions[0]);
        otherBankAccount.deposit(amount, false);
        otherBankAccount.getHistory().add(transactions[1]);
    }

    private Transaction findTransactionById(LinkedList<Transaction> history, int id) {
        for (Transaction t : history) {
            if (t.getId() == id) return t;
        }
        return null;
    }
}