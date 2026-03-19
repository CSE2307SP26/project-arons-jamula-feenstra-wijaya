package main;

import java.util.HashMap;
import java.util.Scanner;

public class MainMenu {

    private static final int EXIT_SELECTION = 5;
	private static final int MAX_SELECTION = 5;

	private BankAccount userAccount;
    private Scanner keyboardInput;
    
    private HashMap<String, BankAccount> userAccounts; // HashMap that stores all of the user's bankaccounts. Each BankAccount can be retrieved using its name.

    public MainMenu() {
        this.userAccount = new BankAccount("default");
        this.userAccounts = new HashMap<>();
        userAccounts.put("default", this.userAccount);
        this.keyboardInput = new Scanner(System.in);
    }

    public void displayOptions() {
        System.out.println("\nWelcome to the 2307 Bank App!");
        System.out.println("You are currently on account: " + userAccount.getName() + "\n");
        
        System.out.println("1. Make a deposit");
        System.out.println("2. Check account balance");
        System.out.println("3. Switch accounts");
        System.out.println("4. Create a new account");
        System.out.println("5. Exit the app");

    }

    public int getUserSelection(int max) {
        int selection = -1;
        while(selection < 1 || selection > max) {
            System.out.print("Please make a selection: ");
            selection = keyboardInput.nextInt();
        }
        return selection;
    }

    public void processInput(int selection) {
        switch (selection) {
            case 1:
                performDeposit();
                break;
            case 2:
                performBalanceCheck();
                break;
            case 3:
                performSwitchAccount();
                break;
            case 4:
                performCreateNewAccount();
                break;
        }
    }

    public void performDeposit() {
        double depositAmount = -1;
        while(depositAmount < 0) {
            System.out.print("How much would you like to deposit: ");
            depositAmount = keyboardInput.nextInt();
        }
        userAccount.deposit(depositAmount);
    }

    public void performBalanceCheck() {
        userAccount.checkBalance();
    }

    public void performSwitchAccount() {
        System.out.println("Here are your current accounts:");
        for (String name : userAccounts.keySet()) {
            System.out.println(name);
        }
        System.out.print("Please enter the name of the account you'd like to switch to: ");
        String choice = keyboardInput.next();
        while(!userAccounts.containsKey(choice)) {
            System.out.println("Account with that name does not exist.");
            System.out.print("Please enter the name of the account you'd like to switch to: ");
            choice = keyboardInput.next();
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

    public void run() {
        int selection = -1;
        while(selection != EXIT_SELECTION) {
            displayOptions();
            selection = getUserSelection(MAX_SELECTION);
            processInput(selection);
        }
    }

    public static void main(String[] args) {
        MainMenu bankApp = new MainMenu();
        bankApp.run();
    }

}
