package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PreloginUI {
    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private static final Scanner SCANNER = new Scanner(System.in);
    public PreloginUI(String serverURL) {
        ServerFacade serverFacade = new ServerFacade(serverURL);
    }

    public void run() {

    }

    public static void register() {

    }

    public static void login() {

    }

    public void quit() {

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
