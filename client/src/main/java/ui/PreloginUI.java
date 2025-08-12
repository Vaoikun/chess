package ui;

import httpresponse.LoginResponse;
import httpresponse.MessageResponse;
import httpresponse.RegisterResponse;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

public class PreloginUI {
    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private static final Scanner SCANNER = new Scanner(System.in);
    public PreloginUI(String serverURL) {
        ServerFacade serverFacade = new ServerFacade(serverURL);
    }

    public void run() {
        OUT.println("Welcome to the Chess server!");
        OUT.println();
        OUT.println(help());
        OUT.println();
        OUT.println("Enter command below.");
        String input = SCANNER.nextLine();
        while (!Objects.equals(input, "Quit")) {
            this.eval(input);
            input = SCANNER.nextLine();
        }
    }

    public static void register() {
        OUT.println("Set username below.");
        String username = SCANNER.nextLine();
        OUT.println("Set password below.");
        String password = SCANNER.nextLine();
        OUT.println("Set email below.");
        String email = SCANNER.nextLine();
        try {
            Object registerResult = ServerFacade.register(username, password, email);
            if (registerResult instanceof RegisterResponse registerResponse) {
                String authToken = registerResponse.authToken();
                PostloginUI postloginUI = new PostloginUI("http://localhost:8080", authToken);
                OUT.println("Registration successful!");
                OUT.println();
                OUT.println("Logging you in...");
                postloginUI.run();
            } else {
                MessageResponse messageResponse = (MessageResponse) registerResult;
                OUT.println(messageResponse.message());
                OUT.println();
                OUT.println(help());
                OUT.println();
                OUT.println("Enter command below.");
            }
        } catch (IOException e) {
            OUT.println(e.getMessage());
        }
    }

    public static void login() {
        OUT.println("Enter username below.");
        String username = SCANNER.nextLine();
        OUT.println("Enter password below.");
        String password = SCANNER.nextLine();
        try {
            Object loginResult = ServerFacade.login(username, password);
            if (loginResult instanceof LoginResponse loginResponse) {
                String authToken = loginResponse.authToken();
                PostloginUI postloginUI = new PostloginUI("http://localhost:8080", authToken);
                OUT.println("Logging in...");
                postloginUI.run();
            } else {
                MessageResponse messageResponse = (MessageResponse) loginResult;
                OUT.println(messageResponse.message());
                OUT.println();
                OUT.println(help());
                OUT.println();
                OUT.println("Enter command below.");
            }
        } catch (IOException e) {
            OUT.println(e.getMessage());
        }
    }

    public void quit() {
        OUT.println("Thank you for playing...");
        System.exit(0);
    }


    public static String help () {
        return """
                   Register (USERNAME) (PASSWORD) (EMAIL): Create an account.
                   Login (USERNAME) (PASSWORD): Login.
                   Quit: Exit the server.
                   Help: Show commands.
                   """;
    }

    public void eval (String input) {
        switch (input) {
            case "Help" -> OUT.println(help());
            case "Register" -> register();
            case "Login" -> login();
            case "Quit" -> quit();
            default -> OUT.println(help());
        }
    }
}
