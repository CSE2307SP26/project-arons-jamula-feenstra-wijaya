package main;

// New class to support transaction history with more detailed information about each transaction.
public class Transaction {

    private static int nextId = 1;

    private final int id;
    private final String type;
    private final String description;
    private final double amount;
    private final String user;
    private final String userAccount;
    private int linkedId;

    public Transaction(String type, String description, double amount, String relatedUser, String relatedAccount) {
        this.id = nextId++;
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.user = relatedUser;
        this.userAccount = relatedAccount;
        this.linkedId = -1;
    }

    public Transaction(String type, String description, double amount) {
        this(type, description, amount, null, null);
    }

    public int getId()             { return id; }
    public String getType()        { return type; }
    public String getDescription() { return description; }
    public double getAmount()      { return amount; }
    public String getRelatedUser() { return user; }
    public String getRelatedAccount() { return userAccount; }
    public int getLinkedId()       { return linkedId; }
    public void setLinkedId(int linkedId) { this.linkedId = linkedId; }

    @Override
    public String toString() { return description; }
}
