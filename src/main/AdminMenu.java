package main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class AdminMenu {

    /*--------------------------------------------------------
                            Constants
    ---------------------------------------------------------*/
    private static final int EXIT_SELECTION = 0;
    private static final int MAX_SELECTION = 5;

    /*--------------------------------------------------------
                            Fields
    ---------------------------------------------------------*/
    private Scanner keyboardInput;
    private HashMap<String, User> userDatabase;

    /*--------------------------------------------------------
                          Constructor
    ---------------------------------------------------------*/
    public AdminMenu(HashMap<String, User> userDatabase) {
        this.userDatabase = userDatabase;
        this.keyboardInput = new Scanner(System.in);
    }

    /*--------------------------------------------------------
                        Menu / Navigation
    ---------------------------------------------------------*/
    public void displayOptions() {  
        System.out.println("\nWelcome to the 2307 Bank App! --- ADMIN ---");
        System.out.println("1. Collect fees");
        System.out.println("2. Apply interest");
        System.out.println("3. List all accounts");
        System.out.println("4. Void inter-user transfer");
        System.out.println("5. Unlock user account");
        System.out.println("0. Exit the app");
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
            case 4: voidTransaction(); break;
            case 5: unlockUserAccount(); break;
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

    private void voidTransaction() {
        User senderUser = promptForUser();
        if (senderUser == null) return;

        BankAccount senderAcct = promptForUserAccount(senderUser, "select sender account");
        if (senderAcct == null) return;

        Transaction transactionToVoid = pickTransaction(senderAcct, senderUser.getUsername());
        if (transactionToVoid == null) return;

        BankAccount recipientAcct = findRecipientAccount(transactionToVoid);
        if (recipientAcct == null) return;

        if (!canReverseTransfer(recipientAcct, transactionToVoid.getAmount())) return;

        reverseTransfer(senderAcct, recipientAcct, senderUser.getUsername(), transactionToVoid);

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
        System.out.println("User '" + user.getUsername() + "' has been unlocked and their failed attempts have been reset.");
    }

    /*--------------------------------------------------------
                    Void Transaction Helpers
    ---------------------------------------------------------*/
    private Transaction pickTransaction(BankAccount account, String username) {
        LinkedList<Transaction> history = account.getHistory();
        if (history.isEmpty()) {
            System.out.println("No transactions on this account.");
            return null;
        }
        System.out.println("\nTransaction history (" + username + " / " + account.getName() + "):");
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i).getDescription());
        }
        int lineNum = getLineSelection(history.size());
        if (lineNum == 0) return null;
        Transaction selected = history.get(lineNum - 1);
        if (!selected.getType().equals("inter-user-transfer")) {
            System.out.println("Only inter-user transactions can be voided!");
            return null;
        }
        return selected;
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

    private BankAccount findRecipientAccount(Transaction transactionToVoid) {
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

    private void reverseTransfer(BankAccount senderAcct, BankAccount recipientAcct,
                                 String senderUsername, Transaction transactionToVoid) {
        Transaction receptionToVoid =
                findTransactionById(recipientAcct.getHistory(), transactionToVoid.getLinkedId());
        recipientAcct.withdraw(transactionToVoid.getAmount(), false);
        senderAcct.deposit(transactionToVoid.getAmount(), false);
        senderAcct.getHistory().remove(transactionToVoid);
        if (receptionToVoid != null) recipientAcct.getHistory().remove(receptionToVoid);
        senderAcct.getHistory().add(new Transaction("void",
                String.format("VOID (admin): Reversed transfer of $%.2f to %s (%s)",
                        transactionToVoid.getAmount(),
                        transactionToVoid.getRelatedUser(),
                        transactionToVoid.getRelatedAccount()),
                        transactionToVoid.getAmount()));
        recipientAcct.getHistory().add(new Transaction("void",
                String.format("VOID (admin): Reversed transfer of $%.2f from %s (%s)",
                        transactionToVoid.getAmount(),
                        senderUsername,
                        senderAcct.getName()),
                        transactionToVoid.getAmount()));
    }

    private Transaction findTransactionById(LinkedList<Transaction> history, int id) {
        for (Transaction t : history) {
            if (t.getId() == id) return t;
        }
        return null;
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