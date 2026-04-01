package main;

import java.util.HashMap;
import java.util.Scanner;

public class BankApplication {

    private HashMap<String, BankAccount> sharedAccountsMap;
    private Scanner keyboardInput;
    private static final int EXIT_SELECTION = 0;
	private static final int MAX_SELECTION = 2;

    public BankApplication() {
        this.sharedAccountsMap = new HashMap<>();
        this.sharedAccountsMap.put("default", new BankAccount("default"));
        this.keyboardInput = new Scanner(System.in);
    }

    public void displayOptions() {
        System.out.println("\n=== Bank System Login ===");
        System.out.println("1. User Login");
        System.out.println("2. Admin Login");
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
                System.out.println("\n--- Entering User Mode ---");
                MainMenu userMenu = new MainMenu(sharedAccountsMap);
                userMenu.run(); 
                break;
            case 2:
                System.out.println("\n--- Entering Admin Mode ---");
                AdminMenu adminMenu = new AdminMenu(sharedAccountsMap);
                adminMenu.run();
                break;
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