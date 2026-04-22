package main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Supports transaction history with detailed information
public class Transaction {

    /*--------------------------------------------------------
                            Constants
    ---------------------------------------------------------*/
    private static int nextId = 1;
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /*--------------------------------------------------------
                            Fields
    ---------------------------------------------------------*/
    private final int id;
    private final String type;
    private final String description;
    private final double amount;
    private final String user;
    private final String userAccount;
    private final String note;
    private int linkedId;
    private final LocalDateTime timestamp;

    /*--------------------------------------------------------
                          Constructors
    ---------------------------------------------------------*/
    public Transaction(String type, String description, double amount,
                       String relatedUser, String relatedAccount, String note) {
        this.id = nextId++;
        this.type = type;
        this.description = description;
        this.amount = amount;
        this.user = relatedUser;
        this.userAccount = relatedAccount;
        this.note = note;
        this.linkedId = -1;
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(String type, String description, double amount) {
        this(type, description, amount, null, null, null);
    }

    /*--------------------------------------------------------
                            Getters
    ---------------------------------------------------------*/
    public int getId() { 
        return id; 
    }
    public String getType() { 
        return type;
}
    public String getDescription() { 
        return description; 
    }
    public double getAmount() { 
        return amount; 
    }
    public String getRelatedUser() { 
        return user; 
    }
    public String getRelatedAccount() { 
        return userAccount; 
    }
    public String getNote() { 
        return note; 
    }
    public int getLinkedId() {
        return linkedId;
    }
    public String getTimestamp() {
        return timestamp.format(TIMESTAMP_FORMAT);
    }

    /*--------------------------------------------------------
                            Setters
    ---------------------------------------------------------*/
    public void setLinkedId(int linkedId) {
        this.linkedId = linkedId;
    }

    /*--------------------------------------------------------
                            Overrides
    ---------------------------------------------------------*/
    @Override
    public String toString() {
        return description;
    }
}