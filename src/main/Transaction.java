package main;

// Supports transaction history with detailed information
public class Transaction {

    /*--------------------------------------------------------
                            Constants
    ---------------------------------------------------------*/
    private static int nextId = 1;

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