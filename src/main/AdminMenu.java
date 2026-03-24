package main;

import java.util.HashMap;
import java.util.Scanner;

public class AdminMenu {

    private static final int EXIT_SELECTION = 0;
	private static final int MAX_SELECTION = 2;

	private BankAccount userAccount;
    private Scanner keyboardInput;
    
    private HashMap<String, BankAccount> userAccounts; // HashMap that stores all of the user's bankaccounts. Each BankAccount can be retrieved using its name.

    public AdminMenu(HashMap<String, BankAccount> sharedAccountsMap) {
        this.userAccounts = sharedAccountsMap;
        this.userAccount = userAccounts.get("default");
        this.keyboardInput = new Scanner(System.in);
    }

    public void displayOptions() {
        System.out.println("\nWelcome to the 2307 Bank App! --- ADMIN ---");
        System.out.println("You are currently on account: " + userAccount.getName() + "\n");
        
        System.out.println("1. Collect fees");
        System.out.println("2. Apply interest");
        System.out.println("3. Create a new account");
        System.out.println("4. Switch accounts");
        System.out.println("0. Exit the app");

    }

    public int getUserSelection(int max) {
        int selection = -1;
        while(selection < 0 || selection > max) {
            System.out.print("Please make a selection: ");
            selection = keyboardInput.nextInt();
        }
        return selection;
    }

    public void processInput(int selection) {
        switch (selection) {
            case 1:
                //performCollectFees();
                break;
            case 2:
                performApplyInterest();
                break;
            case 3:
                performCreateNewAccount();
                break;
            case 4:
                performSwitchAccount();
                break;
        }
    }

    public void performSwitchAccount() {
        System.out.println("Here are your current accounts:");
        for (String name : userAccounts.keySet()) {
            System.out.println(name);
        }
        System.out.print("Please enter the name of the account you'd like to switch to (Type 'cancel' to cancel): ");
        String choice = keyboardInput.next();
        while(!userAccounts.containsKey(choice) && !choice.equals("cancel")) {
            System.out.println("Account with that name does not exist.");
            System.out.print("Please enter the name of the account you'd like to switch to: ");
            choice = keyboardInput.next();
        }
        if(choice.equals("cancel")) {
            return;
        }
        this.userAccount = userAccounts.get(choice);
    }

    public void performCreateNewAccount() {
        System.out.println("Please enter the name of your new account:");
        String name = keyboardInput.next();

        while(userAccounts.containsKey(name)) {
            System.out.println("A bank account already uses that name. Try a different name.");
            name = keyboardInput.next();
        }

        BankAccount newBankAccount = new BankAccount(name);
        userAccounts.put(name, newBankAccount);
        System.out.println("Account " + name + "Successfully created!");
    }
    
    public void performApplyInterest() {
        double interestRate = -1;
        while(interestRate < 0) {
            System.out.print("What interest rate would you like to apply (in %): ");
            interestRate = 0.01 * keyboardInput.nextInt();
        }
        userAccount.applyInterest(interestRate);
    }

    public void run() {
        int selection = -1;
        while(selection != EXIT_SELECTION) {
            displayOptions();
            selection = getUserSelection(MAX_SELECTION);
            processInput(selection);
        }
        keyboardInput.close();
    }
}
