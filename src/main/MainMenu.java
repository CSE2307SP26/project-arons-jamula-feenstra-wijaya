package main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class MainMenu {

    private static final int EXIT_SELECTION = 0;
	private static final int MAX_SELECTION = 10;

	private BankAccount userAccount;
    private Scanner keyboardInput;
    private User currentUser;
    private HashMap<String, BankAccount> userAccounts; // HashMap that stores all of the user's bankaccounts. Each BankAccount can be retrieved using its name.
    private HashMap<String, User> userDatabase;

    public MainMenu(User user, HashMap<String, User> userDatabase) {
        this.currentUser = user;
        this.userDatabase = userDatabase;
        this.userAccounts = user.getAllAccounts();
        this.userAccount = userAccounts.values().iterator().next();
        this.keyboardInput = new Scanner(System.in);
    }

    public void displayOptions() {
        System.out.println("\nWelcome to the 2307 Bank App! (Logged in as: " + currentUser.getUsername() + ")");
        System.out.println("You are currently on account: " + userAccount.getName() + "\n");
        
        System.out.println("1. Make a deposit");
        System.out.println("2. Withdraw funds");
        System.out.println("3. Check account balance");
        System.out.println("4. View transaction history");
        System.out.println("5. Transfer funds between accounts");
        System.out.println("6. Transfer funds to another user's account");
        System.out.println("7. Create a new account");
        System.out.println("8. Switch accounts");
        System.out.println("9. Close an account");
        System.out.println("10. Change user password");
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
                deposit();
                break;
            case 2:
                withdraw();
                break;
            case 3:
                showBalance();
                break;
            case 4:
                viewTransactionHistory();
                break;
            case 5:
                transfer();
                break;
            case 6:
                transferToAnotherUser();
                break;
            case 7:
                createAccount();
                break;
            case 8:
                switchAccount();
                break;
            case 9:
                closeAccount();
                break;
            case 10:
                changeUserPassword();
                break;
        }
    }

    /*--------------------------------------------------------
                            Core Actions
    ---------------------------------------------------------*/

    private void deposit() {
        double amount = getPositiveDouble("Enter deposit amount: ");
        userAccount.deposit(amount);
    }

    private void withdraw() {
        double amount = getPositiveDouble("Enter withdrawal amount: ");
        if(amount > userAccount.getBalance()) {
            System.out.println("Insufficient funds. Operation cancelled.");
            return;
        }
        userAccount.withdraw(amount);
    }

    private void showBalance() {
        double balance = userAccount.getBalance();

        if (balance < 0) {
            System.out.printf("Balance: -$%.2f%n", Math.abs(balance));
        } else {
            System.out.printf("Balance: $%.2f%n", balance);
        }
    }

    private void transfer() {
        if (canTransfer() == false) return;

        String targetName = getExistingAccountName("Enter account to transfer to (or type 'cancel' to cancel): ");

        if (targetName.equals("cancel")) {
            System.out.println("Transfer cancelled.");
            return;
        }
        if (targetName.equals(userAccount.getName())) {
            System.out.println("Cannot transfer to the same account.");
            return;
        }

        double amount = getValidTransferAmount();

        userAccount.transfer(userAccounts.get(targetName), amount);
    }

    private void transferToAnotherUser() {
        if (!canTransferToAnotherUser()) return;

        System.out.print("Enter recipient's username (or type 'cancel' to cancel): ");
        String recipientUsername = keyboardInput.nextLine().trim();

        if (recipientUsername.equalsIgnoreCase("cancel")) {
            System.out.println("Transfer cancelled.");
            return;
        }

        if (recipientUsername.equals(currentUser.getUsername())) {
            System.out.println("Use 'Option 5: Transfer funds between accounts' to transfer money between your own accounts.");
            return;
        }

        if (!userDatabase.containsKey(recipientUsername)) {
            System.out.println("Recipient user not found. Transfer cancelled.");
            return;
        }

        User recipientUser = userDatabase.get(recipientUsername);
        HashMap<String, BankAccount> recipientAccounts = recipientUser.getAllAccounts();

        if (recipientAccounts.isEmpty()) {
            System.out.println("Recipient has no accounts. Transfer cancelled.");
            return;
        }

        String targetAccountName = getExistingAccountNameInMap(
                recipientAccounts, "Enter recipient account name (or type 'cancel' to cancel): ");

        if (targetAccountName.equals("cancel")) {
            System.out.println("Transfer cancelled.");
            return;
        }

        double amount = getValidTransferAmount();

        userAccount.transfer(recipientAccounts.get(targetAccountName), amount);
    }

    private void viewTransactionHistory() {
        LinkedList<String> history = userAccount.getHistory();

        System.out.println("Transaction history:");
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i));
        }
    }

    private void createAccount() {
        String name;
        do {
            System.out.print("Enter new account name (or type 'cancel' to cancel): ");
            name = keyboardInput.nextLine();
            if(userAccounts.containsKey(name)) {
                System.out.println("An account with that name already exists. Try again.");
            }
            if(name.trim().isEmpty()) {
                System.out.println("Account name cannot be empty. Try again.");
            }
            if(name.equalsIgnoreCase("cancel")) {
                System.out.println("Account creation cancelled.");
                return;
            }
        } while (userAccounts.containsKey(name) || name.trim().isEmpty());

        userAccounts.put(name, new BankAccount(name));
        System.out.println("Account '" + name + "' created.");
    }

    private void switchAccount() {
        System.out.println("Available accounts:");
        for (String name : userAccounts.keySet()) {
            System.out.println("- " + name);
        }

        String choice = getExistingAccountName("Enter account name (or type 'cancel' to cancel): ");

        if (!choice.equalsIgnoreCase("cancel")) {
            userAccount = userAccounts.get(choice);
        }
    }


    private void closeAccount() {

        if(confirmClose().equals("no")) {
            System.out.println("Account closure cancelled.");
            return;
        }

        if (canCloseAccount() == false) {
            return;
        }

        String name = userAccount.getName();
        userAccounts.remove(name);
        userAccount = userAccounts.values().iterator().next();

        System.out.println("Account '" + name + "' closed.");
    }

    private void changeUserPassword() {
        System.out.print("Enter current password: ");
        String currentPassword = keyboardInput.nextLine();

        if (currentUser.login(currentPassword) == false) {
            System.out.println("Incorrect password. Password change cancelled.");
            return;
        }

        System.out.print("Enter new password: ");
        String newPassword = keyboardInput.nextLine();

        currentUser.changePassword(newPassword);
        System.out.println("Password changed successfully.");
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

    private double getValidTransferAmount() {
        double amount;
        do {
            amount = getPositiveDouble("Enter transfer amount: ");
            if (amount > userAccount.getBalance()) {
                System.out.println("Insufficient funds. Try again.");
            }
        } while (amount > userAccount.getBalance());

        return amount;
    }

    private boolean canTransfer() {
        if(userAccounts.size() == 1) {
            System.out.println("No other accounts available to transfer to. Operation cancelled.");
            return false;
        }
        if(userAccount.getBalance() <= 0) {
            System.out.println("Insufficient funds to make a transfer. Operation cancelled.");
            return false;
        }
        return true;
    }

    private boolean canTransferToAnotherUser() {
        if (userAccount.getBalance() <= 0) {
            System.out.println("Insufficient funds to make a transfer. Operation cancelled.");
            return false;
        }
        return true;
    }

    private String getExistingAccountName(String prompt) {
        String name;

        while (true) {
            System.out.print(prompt);
            name = keyboardInput.nextLine();

            if (name.equalsIgnoreCase("cancel")) {
                return "cancel";
            }

            if (userAccounts.containsKey(name)) {
                return name;
            }

            System.out.println("Account not found. Try again.");
        }
    }

    private String getExistingAccountNameInMap(HashMap<String, BankAccount> accounts, String prompt) {
        String name;

        while (true) {
            System.out.print(prompt);
            name = keyboardInput.nextLine();

            if (name.equalsIgnoreCase("cancel")) {
                return "cancel";
            }

            if (accounts.containsKey(name)) {
                return name;
            }

            System.out.println("Account not found. Try again.");
        }
    }

    private String confirmClose() {
        String choice;

        do {
            System.out.print("Close account '" + userAccount.getName() + "'? (yes/no): ");
            choice = keyboardInput.nextLine();
            if(!choice.equalsIgnoreCase("yes") && !choice.equalsIgnoreCase("no")) {
                System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            }
        } while (!choice.equalsIgnoreCase("yes") && !choice.equalsIgnoreCase("no"));

        return choice.toLowerCase();
    }

    private boolean canCloseAccount() {
        if (userAccount.getBalance() != 0) {
            System.out.println("Operation cancelled. Account balance must be 0.");
            return false;
        }

        if (userAccounts.size() == 1) {
            System.out.println("Operation cancelled. Cannot delete the only account.");
            return false;
        }

        return true;
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
