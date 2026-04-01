package main;

import java.util.HashMap;
import java.util.Scanner;

public class AdminMenu {

    private static final int EXIT_SELECTION = 0;
	private static final int MAX_SELECTION = 4;

    private Scanner keyboardInput;
    
    private HashMap<String, BankAccount> userAccounts; // HashMap that stores all of the user's bankaccounts. Each BankAccount can be retrieved using its name.

    public AdminMenu(HashMap<String, BankAccount> sharedAccountsMap) {
        this.userAccounts = sharedAccountsMap;
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
        BankAccount account = promptForAccount("collect fees from");
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
        BankAccount account = promptForAccount("apply interest to");
        if(account == null) return;

        double interestRate = getPositiveDouble("Enter interest rate to apply (in %): ");

        account.applyInterest(interestRate / 100);
        System.out.println("Applied " + interestRate + "% interest to " + account.getName() + ".");
    }

    public void listAccounts() {
        System.out.println("List of accounts and their balances:");
        for(String accountName : userAccounts.keySet()) {
            BankAccount account = userAccounts.get(accountName);
            if(account.getBalance() < 0) {
                System.out.println("- " + account.getName() + ": -$" + Math.abs(account.getBalance()));
            } else {
                System.out.println("- " + account.getName() + ": $" + account.getBalance());
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

    private BankAccount promptForAccount(String actionName) {
    String accountName;
    do {
        System.out.print("Enter account name to " + actionName + " (or type 'cancel' to cancel): ");
        accountName = keyboardInput.nextLine();

        if(accountName.equalsIgnoreCase("cancel")) {
            System.out.println(actionName + " cancelled.");
            return null;
        }

        if(!userAccounts.containsKey(accountName)) {
            System.out.println("Account not found. Please try again.");
        }

    } while (!userAccounts.containsKey(accountName));

    return userAccounts.get(accountName);
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
