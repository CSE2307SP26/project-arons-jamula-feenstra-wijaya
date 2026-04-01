package main;

import java.util.HashMap;
import java.util.Scanner;

public class BankApplication {

    private HashMap<String, BankAccount> sharedAccountsMap;
    private HashMap<String, User> userDatabase;
    private Scanner keyboardInput;
    private static final int EXIT_SELECTION = 0;
	private static final int MAX_SELECTION = 3;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "123";

    public BankApplication() {
        this.sharedAccountsMap = new HashMap<>();
        this.sharedAccountsMap.put("default", new BankAccount("default"));
        this.userDatabase = new HashMap<>();
        this.keyboardInput = new Scanner(System.in);
    }

    public void displayOptions() {
        System.out.println("\n=== Bank System ===");
        System.out.println("1. User Login");
        System.out.println("2. Register User");
        System.out.println("3. Admin Login");
        System.out.println("0. Shut Down");
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
                userLoginMenu();
                break;
            case 2:
                registerUser();
                break;
            case 3:
                adminLoginMenu();
                break;
        }
    }

    private void userLoginMenu() {
        System.out.println("\n=== User Login ===");
        System.out.print("Enter username: ");
        String username = keyboardInput.nextLine().trim();

        if (userDatabase.containsKey(username)) {
            System.out.print("Enter password: ");
            String password = keyboardInput.nextLine();
            User user = userDatabase.get(username);

            if (user.login(password)) {
                System.out.println("\n--- Login Successful ---");
                MainMenu userMenu = new MainMenu(user);
                userMenu.run();
            } else {
                System.out.println("Invalid password. Try again.");
            }
        } else {
            System.out.println("\nUser not found.");
        }
    }

    private void registerUser() {
        System.out.println("\n=== Register New User ===");
        System.out.print("Enter username: ");
        String username = keyboardInput.nextLine().trim();
        if (userDatabase.containsKey(username)) {
            System.out.println("Username already exists. Try another username.");
            return;
        }
        System.out.print("Enter password: ");
        String password = keyboardInput.nextLine();
        User newUser = new User(username, password);
        BankAccount defaultAccount = new BankAccount(username);
        newUser.addAccount(defaultAccount);
        userDatabase.put(username, newUser);
        System.out.println("\nRegistration successful! Entering User Menu...");
        MainMenu userMenu = new MainMenu(newUser);
        userMenu.run();
    }

    private void adminLoginMenu() {
        System.out.println("\n=== Admin Login ===");
        System.out.print("Enter admin username: ");
        String username = keyboardInput.nextLine().trim();
        System.out.print("Enter admin password: ");
        String password = keyboardInput.nextLine();

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            System.out.println("\n--- Admin Login Successful ---");
            AdminMenu adminMenu = new AdminMenu(userDatabase);
            adminMenu.run();
        } else {
            System.out.println("Invalid admin credentials. Try again.");
        }
    }

    public void run() {
        int selection = -1;
        while (selection != EXIT_SELECTION) {
            displayOptions();
            selection = getUserSelection(MAX_SELECTION);
            processInput(selection);
        }
        keyboardInput.close();
    }

    public static void main(String[] args) {
        BankApplication app = new BankApplication();
        app.run();
    }
}