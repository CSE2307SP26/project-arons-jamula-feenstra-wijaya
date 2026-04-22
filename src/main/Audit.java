package main;

public class Audit {

    private static int nextId = 1;

    private final int id;
    private final String action;
    private final String targetUsername;
    private final String targetAccount;
    private final String details;

    public Audit(String action, String targetUsername, String targetAccount, String details) {
        this.id = nextId++;
        this.action = action;
        this.targetUsername = targetUsername;
        this.targetAccount = targetAccount;
        this.details = details;
    }

    public int getId()               { return id; }
    public String getAction()        { return action; }
    public String getTargetUser()    { return targetUsername; }
    public String getTargetAccount() { return targetAccount; }
    public String getDetails()       { return details; }

    @Override
    public String toString() { return details; }
}
