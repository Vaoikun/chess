package ui;

import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GameplayUI {
    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private static final Scanner SCANNER = new Scanner(System.in);

    public GameplayUI (String serverURL, String authToken, ChessGame.TeamColor color, int gameID) {
        ServerFacade serverFacade = new ServerFacade(serverURL);
    }

    public void run () {

    }

    public void reload () {

    }

    public void makeMove() {

    }

    public void highlight () {

    }

    public void leave () {

    }

    public void resign () {

    }

    public static String help () {
        return """
                Reload: Reload the chess board.
                Make Move: Make a move.
                Highlight Moves: Highlight possible moves.
                Leave: Leave the game.
                Resign: Resign from the game.
                Help: Show commands.""";
    }

    public void eval (String input) {
        switch (input) {
            case "Reload" -> reload();
            case "Make Move" -> makeMove();
            case "Highlight Moves" -> highlight();
            case "Leave" -> leave();
            case "Resign" -> resign();
            case "Help" -> OUT.println(help());
            default -> OUT.println(help());
        }
    }
}
