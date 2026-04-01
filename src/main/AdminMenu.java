package main;

import java.util.HashMap;
import java.util.Scanner;

public class AdminMenu {

    private static final int EXIT_SELECTION = 0;
	private static final int MAX_SELECTION = 4;

    private Scanner keyboardInput;
    
    private HashMap<String, User> userDatabase; // HashMap that stores all users and their accounts

    public AdminMenu(HashMap<String, User> userDatabase) {
        this.userDatabase = userDatabase;
        this.keyboardInput = new Scanner(System.in);
    }

    public void displayOptions() {  
        System.out.println("\nWelcome to the 2307 Bank App! --- ADMIN ---");
        
        System.out.println("1. Collect fees");
        System.out.println("2. Apply interest");
        System.out.println("3. List all accounts");
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
            keyboardInput.nextLine(); // clear buffer
        }
        return selection;
    }

    public void processInput(int selection) {
        switch (selection) {
            case 1:
                collectFees();
                break;
            case 2:
                applyInterest();
                break;
            case 3:
                listAccounts();
                break;
        }
    }

    /*--------------------------------------------------------
                            Core Actions
    ---------------------------------------------------------*/

    public void collectFees() {
        User user = promptForUser();
        if(user == null) return;

        BankAccount account = promptForAccountFromUser(user, "collect fees from");
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

        BankAccount account = promptForAccountFromUser(user, "apply interest to");
        if(account == null) return;

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

    /*--------------------------------------------------------
                            Helper Methods
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
            keyboardInput.nextLine(); // clear buffer
            
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

    private BankAccount promptForAccountFromUser(User user, String actionName) {
        HashMap<String, BankAccount> accounts = user.getAllAccounts();
        String accountName;
        do {
            System.out.print("Enter account name to " + actionName + " (or type 'cancel' to cancel): ");
            accountName = keyboardInput.nextLine().trim();

            if(accountName.equalsIgnoreCase("cancel")) {
                System.out.println(actionName + " cancelled.");
                return null;
            }

            if(!accounts.containsKey(accountName)) {
                System.out.println("Account not found. Try again.");
            }

        } while (!accounts.containsKey(accountName));
        BankAccount account = accounts.get(accountName);
        return account;
    }

    /*--------------------------------------------------------
                            Main Loop
    ---------------------------------------------------------*/
    public void run() {
        int selection = -1;
        while(selection != EXIT_SELECTION) {
            displayOptions();
            selection = getUserSelection(MAX_SELECTION);
            processInput(selection);
        }
    }
}
