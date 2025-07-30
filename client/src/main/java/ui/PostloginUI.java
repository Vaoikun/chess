package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PostloginUI {
    private final String authToken;
    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private static final Scanner SCANNER = new Scanner(System.in);

    public PostloginUI (String serverURL, String authToken) {
         ServerFacade serverFacade = new ServerFacade(serverURL);
         this.authToken = authToken;
    }

    public void createGame() {

    }

    public void joinGame () {

    }

    public void listGames () {

    }

    public void observeGame () {

    }

    public void logout () {

    }

    public static String help() {
        return """
                To create a game: Create Game <Name>
                To join a game: Join Game <GameID>
                To list games: List Games
                To observe a game: Observe <GameID>
                To logout: Logout
                To show commands: Help
                """;
    }

    public void eval(String input) {
        switch (input) {
            case "Help" -> OUT.println(help());
            case "Create Game" -> createGame();
            case "Join Game" -> joinGame();
            case "List Games" -> listGames();
            case "Observe" -> observeGame();
            case "Logout" -> logout();
            default -> OUT.println(help());
        }
    }

    public void run() {

    }
}
