package main;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Represents a single admin action in the audit log, similar to Transaction for accounts
public class Audit {

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
    private final String action;
    private final String targetUsername;
    private final String targetAccount;
    private final String details;
    private final LocalDateTime timestamp;

    /*--------------------------------------------------------
                          Constructor
    ---------------------------------------------------------*/
    public Audit(String action, String targetUsername, String targetAccount, String details) {
        this.id = nextId++;
        this.action = action;
        this.targetUsername = targetUsername;
        this.targetAccount = targetAccount;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    /*--------------------------------------------------------
                            Getters
    ---------------------------------------------------------*/
    public int getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getTargetUser() {
        return targetUsername;
    }

    public String getTargetAccount() {
        return targetAccount;
    }

    public String getDetails() {
        return details;
    }

    public String getTimestamp() {
        return timestamp.format(TIMESTAMP_FORMAT);
    }

    /*--------------------------------------------------------
                            Overrides
    ---------------------------------------------------------*/
    @Override
    public String toString() {
        return details;
    }
}
