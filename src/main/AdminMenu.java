package main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class AdminMenu {

    /*--------------------------------------------------------
                            Constants
    ---------------------------------------------------------*/
    private static final int EXIT_SELECTION = 0;
    private static final int MAX_SELECTION = 8;

    /*--------------------------------------------------------
                            Fields
    ---------------------------------------------------------*/
    private Scanner keyboardInput;
    private HashMap<String, User> userDatabase;
    private LinkedList<Audit> auditHistory;

    /*--------------------------------------------------------
                          Constructor
    ---------------------------------------------------------*/
    public AdminMenu(HashMap<String, User> userDatabase) {
        this(userDatabase, new LinkedList<>());
    }

    public AdminMenu(HashMap<String, User> userDatabase, LinkedList<Audit> auditHistory) {
        this.userDatabase = userDatabase;
        this.auditHistory = auditHistory;
        this.keyboardInput = new Scanner(System.in);
    }

    /*--------------------------------------------------------
                        Menu / Navigation
    ---------------------------------------------------------*/
    public void displayOptions() {  
        System.out.println("\nWelcome to the 2307 Bank App! --- ADMIN ---");
        System.out.println("--------------------------------------------------");
        System.out.println("1. Collect fees");
        System.out.println("2. Apply interest");
        System.out.println("3. List all accounts");
        System.out.println("4. Undo most recent transaction");
        System.out.println("5. Void inter-user transfer");
        System.out.println("6. Unlock user account");
        System.out.println("7. View audit history");
        System.out.println("8. Provide user with recovery password");
        System.out.println("0. Exit to login menu");
        System.out.println("--------------------------------------------------");
    }

    public int getUserSelection(int max) {
        int selection = -1;
        while(selection < 0 || selection > max) {
            System.out.print("Please make a selection: ");
            while (!keyboardInput.hasNextInt()) {
                System.out.println("Invalid input.");
                keyboardInput.next();
            }
            selection = keyboardInput.nextInt();
            if(selection < 0 || selection > max) {
                System.out.println("Invalid selection. Try again.");
            }
            keyboardInput.nextLine();
        }
        return selection;
    }

    public void processInput(int selection) {
        switch (selection) {
            case 1: collectFees(); break;
            case 2: applyInterest(); break;
            case 3: listAccounts(); break;
            case 4: undoRecentTransaction(); break;
            case 5: voidTransfer(); break;
            case 6: unlockUserAccount(); break;
            case 7: viewAuditHistory(); break;
            case 8: createRecoveryPassword(); break;
        }
    }

    public void run() {
        int selection = -1;
        while(selection != EXIT_SELECTION) {
            displayOptions();
            selection = getUserSelection(MAX_SELECTION);
            processInput(selection);
        }
    }

    /*--------------------------------------------------------
                            Core Actions
    ---------------------------------------------------------*/
    public void collectFees() {
        User user = promptForUser();
        if(user == null) return;

        BankAccount account = promptForUserAccount(user, "collect fees from");
        if(account == null) return;

        double amount = getPositiveDouble("Enter fee amount to collect: ");

        if(amount > account.getBalance()) {
            System.out.println("Amount exceeds account balance. Fee collection cancelled.");
            return;
        }

        account.collectFees(amount);
        auditHistory.add(new Audit("FEE_COLLECTED", user.getUsername(), account.getName(),
                String.format("Collected $%.2f in fees. New balance: $%.2f", amount, account.getBalance())));
        System.out.println("Collected $" + amount + " in fees from " + account.getName() + ".");
    }

    public void applyInterest() {
        User user = promptForUser();
        if(user == null) return;

        BankAccount account = promptForUserAccount(user, "apply interest to");
        if(account == null) return;

        if (!account.getAccountType().equals("Savings")){
            System.out.println("Action Denied: Interest can only be applied to Savings Accounts.");
            return;
        }

        double interestRate = getPositiveDouble("Enter interest rate to apply (in %): ");
        account.applyInterest(interestRate / 100);
        auditHistory.add(new Audit("INTEREST_APPLIED", user.getUsername(), account.getName(),
                String.format("Applied %.2f%% interest. New balance: $%.2f", interestRate, account.getBalance())));
        System.out.println("Applied " + interestRate + "% interest to " + account.getName() + ".");
    }

    public void listAccounts() {
        if(userDatabase.isEmpty()) {
            System.out.println("No users registered yet.");
            return;
        }
        System.out.println("\n=== All Users and Their Accounts ===");
        for(String username : userDatabase.keySet()) {
            User user = userDatabase.get(username);
            System.out.println("\nUser: " + username);
            HashMap<String, BankAccount> accounts = user.getAllAccounts();
            for(String accountName : accounts.keySet()) {
                BankAccount account = accounts.get(accountName);
                double balance = account.getBalance();
                if(balance < 0) {
                    System.out.println("  - " + account.getName() + ": -$" + String.format("%.2f", Math.abs(balance)));
                } else {
                    System.out.println("  - " + account.getName() + ": $" + String.format("%.2f", balance));
                }
            }
        }
    }

    private void undoRecentTransaction() {
        User selectedUser = promptForUser();
        if (selectedUser == null) return;
        BankAccount selectedAcct = promptForUserAccount(selectedUser, "select account");
        if (selectedAcct == null) return;
        if (selectedAcct.getHistory().isEmpty()) {
            System.out.println("There are no recent transactions to undo.");
            return;
        }
        Transaction recentTransaction = selectedAcct.getHistory().getLast();
        processInputUndoTransaction(selectedUser, selectedAcct, recentTransaction);
        return;
    }

    private void voidTransfer() {
        User selectedUser = promptForUser();
        if (selectedUser == null) return;
        BankAccount selectedAcct = promptForUserAccount(selectedUser, "select account");
        if (selectedAcct == null) return;
        Transaction transactionToVoid = pickTransfer(selectedAcct, selectedUser.getUsername());
        if (transactionToVoid == null) return;
        BankAccount[] roles = resolveVoidAccounts(selectedAcct, transactionToVoid);
        if (roles == null) return;
        Transaction senderTx = resolveSenderTx(transactionToVoid, roles[0]);
        if (senderTx == null) return;
        if (!canReverseTransfer(roles[1], senderTx.getAmount())) return;
        String senderUsername = transactionToVoid.getType().equals("inter-user-transfer")
                ? selectedUser.getUsername() : transactionToVoid.getRelatedUser();
        roles[0].reverseTransfer(roles[1], senderUsername, senderTx);
        auditHistory.add(new Audit("TRANSFER_VOIDED", senderUsername, roles[0].getName(),
                String.format("Voided $%.2f inter-user transfer to %s (%s). Balances restored.",
                        senderTx.getAmount(), senderTx.getRelatedUser(), senderTx.getRelatedAccount())));
        System.out.println("Transfer voided; balances and histories updated.");
    }

    private void unlockUserAccount() {
        User user = promptForUser();
        if (user == null) return;

        if (!user.isLocked()) {
            System.out.println("User '" + user.getUsername() + "' is not currently locked.");
            return;
        }

        user.unlockAccount();
        auditHistory.add(new Audit("ACCOUNT_UNLOCKED", user.getUsername(), "N/A",
                "Unlocked after too many failed login attempts. Failed attempt counter reset."));
        System.out.println("User '" + user.getUsername() + "' has been unlocked and their failed attempts have been reset.");
    }

    private void viewAuditHistory() {
        if (auditHistory.isEmpty()) {
            System.out.println("No audit history yet.");
            return;
        }
        System.out.println("\n=== Admin Audit History ===");
        for (Audit audit : auditHistory) {
            System.out.println(audit.getId() + ". [" + audit.getTimestamp() + "] [" + audit.getAction() + "]"
                    + " User: " + audit.getTargetUser()
                    + " | Account: " + audit.getTargetAccount()
                    + " | " + audit.getDetails());
        }
    }

    private void createRecoveryPassword(){
        User user = promptForUser();
        if(user == null) return;
        if (!user.isLocked()) {
            System.out.println("Account is not locked");
            return;
        }
        System.out.println("Set the recovery password:");
        String recoveryPassword = keyboardInput.next();
        user.changePassword(recoveryPassword);
        user.setCurrentPasswordIsRecoveryPassword(true);
        user.unlockAccount();
    }

    /*--------------------------------------------------------
                    Undo Recent Transaction Helpers
    ---------------------------------------------------------*/

    private void processInputUndoTransaction(User selectedUser, BankAccount selectedAcct, Transaction tx) {
        switch (tx.getType()) {
            case "deposit":
            case "withdraw":
            case "fee":
            case "interest":
                selectedAcct.undoTransaction(tx);
                auditHistory.add(new Audit("TRANSACTION_UNDONE", selectedUser.getUsername(), selectedAcct.getName(),
                        String.format("Undid %s of $%.2f. New balance: $%.2f",
                                tx.getType(), tx.getAmount(), selectedAcct.getBalance())));
                break;
            case "transfer":
            case "received":
                System.out.println("Transfers between two accounts of the same user can not be undone as of now.");
                break;
            case "inter-user-transfer":
            case "inter-user-receipt":
                System.out.println("Please use the 'Void inter-user transfer' function in the admin menu.");
                break;
            case "void":
                System.out.println("Can not undo a voided transaction.");
                break;
            case "undo":
                System.out.println("Can not undo an undone transaction.");
                break;
        }
    }

    /*--------------------------------------------------------
                    Void Transaction Helpers
    ---------------------------------------------------------*/
    private BankAccount[] resolveVoidAccounts(BankAccount selectedAcct, Transaction tx) {
        if (tx.getType().equals("inter-user-transfer")) {
            BankAccount recipientAcct = findRelatedAccount(tx);
            if (recipientAcct == null) return null;
            return new BankAccount[]{selectedAcct, recipientAcct};
        }
        BankAccount senderAcct = findRelatedAccount(tx);
        if (senderAcct == null) return null;
        return new BankAccount[]{senderAcct, selectedAcct};
    }

    private Transaction resolveSenderTx(Transaction tx, BankAccount senderAcct) {
        if (tx.getType().equals("inter-user-transfer")) return tx;
        for (Transaction t : senderAcct.getHistory()) {
            if (t.getId() == tx.getLinkedId()) return t;
        }
        System.out.println("Linked sender transaction not found. Void cancelled.");
        return null;
    }

    private Transaction pickTransfer(BankAccount account, String username) {
        LinkedList<Transaction> interUserTxs = new LinkedList<>();
        for (Transaction t : account.getHistory()) {
            if (t.getType().equals("inter-user-transfer") || t.getType().equals("inter-user-receipt")) {
                interUserTxs.add(t);
            }
        }
        if (interUserTxs.isEmpty()) {
            System.out.println("No inter-user transfers found on this account.");
            return null;
        }
        System.out.println("\nInter-user transfers (" + username + " / " + account.getName() + "):");
        for (int i = 0; i < interUserTxs.size(); i++) {
            System.out.println((i + 1) + ". " + interUserTxs.get(i).getDescription());
        }
        int lineNum = getLineSelection(interUserTxs.size());
        if (lineNum == 0) return null;
        return interUserTxs.get(lineNum - 1);
    }

    private int getLineSelection(int max) {
        while (true) {
            System.out.print("Enter line number (0 to cancel): ");
            while (!keyboardInput.hasNextInt()) {
                System.out.println("Invalid input.");
                keyboardInput.next();
            }
            int num = keyboardInput.nextInt();
            keyboardInput.nextLine();
            if (num == 0) {
                System.out.println("Cancelled.");
                return 0;
            }
            if (num >= 1 && num <= max) return num;
            System.out.println("Invalid line number. Try again.");
        }
    }

    private BankAccount findRelatedAccount(Transaction transactionToVoid) {
        String recipientUsername = transactionToVoid.getRelatedUser();
        String recipientAccountName = transactionToVoid.getRelatedAccount();
        if (!userDatabase.containsKey(recipientUsername)) {
            System.out.println("Recipient user no longer exists. Void cancelled.");
            return null;
        }
        HashMap<String, BankAccount> accounts =
                userDatabase.get(recipientUsername).getAllAccounts();
        if (!accounts.containsKey(recipientAccountName)) {
            System.out.println("Recipient account no longer exists. Void cancelled.");
            return null;
        }
        return accounts.get(recipientAccountName);
    }

    private boolean canReverseTransfer(BankAccount recipientAcct, double amount) {
        if (recipientAcct.getBalance() + 1e-9 < amount) {
            System.out.println("Recipient balance is too low to reverse this transfer.");
            return false;
        }
        return true;
    }

    /*--------------------------------------------------------
                        General Helpers
    ---------------------------------------------------------*/
    private double getPositiveDouble(String prompt) {
        double value;
        do {
            System.out.print(prompt);
            while (!keyboardInput.hasNextDouble()) {
                System.out.println("Invalid input.");
                keyboardInput.next();
            }
            value = keyboardInput.nextDouble();
            keyboardInput.nextLine();

            if(value <= 0) {
                System.out.println("Amount must be positive. Try again.");
            }
        } while (value <= 0);

        return value;
    }

    private User promptForUser() {
        String username;
        do {
            System.out.print("Enter username (or type 'cancel' to cancel): ");
            username = keyboardInput.nextLine().trim();

            if(username.equalsIgnoreCase("cancel")) {
                System.out.println("Cancelled.");
                return null;
            }

            if(!userDatabase.containsKey(username)) {
                System.out.println("User not found. Please try again.");
            }

        } while (!userDatabase.containsKey(username));

        return userDatabase.get(username);
    }

    private BankAccount promptForUserAccount(User user, String actionName) {
        HashMap<String, BankAccount> accounts = user.getAllAccounts();
        String accountName;
        do {
            System.out.print("Enter account name to " + actionName + " (or type 'cancel'): ");
            accountName = keyboardInput.nextLine().trim();
            if(accountName.equalsIgnoreCase("cancel")) {
                System.out.println(actionName + " cancelled.");
                return null;
            }
            if(!accounts.containsKey(accountName)) {
                System.out.println("Account not found. Try again.");
            }
        } while (!accounts.containsKey(accountName));
        return accounts.get(accountName);
    }
}