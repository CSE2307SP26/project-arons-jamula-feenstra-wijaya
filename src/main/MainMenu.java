package main;

import java.util.HashMap;
import java.util.Scanner;

public class MainMenu {

    private static final int EXIT_SELECTION = 0;
	private static final int MAX_SELECTION = 7;

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
        System.out.println("2. Withdraw funds");
        System.out.println("3. Check account balance");
        System.out.println("4. View transaction history");
        System.out.println("5. Transfer funds between accounts");
        System.out.println("6. Create a new account");
        System.out.println("7. Switch accounts");
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
                performDeposit();
                break;
            case 2:
                performWithdraw();
                break;
            case 3:
                performBalanceCheck();
                break;
            case 4:
                break;
            case 5:
                performTransfer();
                break;
            case 6:
                performCreateNewAccount();
                break;
            case 7:
                performSwitchAccount();
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

    public void performWithdraw() {
        double withdrawAmount = -1;
        while (withdrawAmount <= 0) {
            System.out.print("How much would you like to withdraw: ");
            withdrawAmount = keyboardInput.nextInt();
        }
        userAccount.withdraw(withdrawAmount);
    }

    public void performBalanceCheck() {
        if (userAccount.getBalance()  >= 0) {
            System.out.println("This account has a balance of $" + userAccount.getBalance());
        } else if (userAccount.getBalance() < 0) {
            System.out.println("This account has a balance of -$" + userAccount.getBalance());
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

    public void performTransfer() {
        System.out.println("Please enter the name of the account you'd like to transfer money to:");
        String transferAccount = keyboardInput.next();
        while(!userAccounts.containsKey(transferAccount)) {
            System.out.println("Invalid name.");
            System.out.println("Please enter the name of the account you'd like to transfer money to:");
            transferAccount = keyboardInput.next();
        }
        System.out.println("How much would you like to transfer?");
        int transferAmount = keyboardInput.nextInt();
        while(transferAmount <= 0 || transferAmount > userAccount.getBalance()) {
            System.out.println("Invalid amount.");
            System.out.println("How much would you like to transfer?");
            transferAmount = keyboardInput.nextInt();
        }
        userAccount.transfer(userAccounts.get(transferAccount), transferAmount);
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
