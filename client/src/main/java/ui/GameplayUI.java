package ui;

import chess.ChessBoard;
import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

public class GameplayUI {
    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private static final Scanner SCANNER = new Scanner(System.in);
    private String authToken;
    private ChessGame.TeamColor teamColor;
    private int gameID;

    public GameplayUI (String serverURL, String authToken, ChessGame.TeamColor teamColor, int gameID) {
        ServerFacade serverFacade = new ServerFacade(serverURL);
        this.authToken = authToken;
        this.teamColor = teamColor;
        this.gameID = gameID;
    }

    public void run () {
        OUT.println();
        OUT.println("Welcome to the game!");
        OUT.println();
        reload();
        OUT.println("Enter command...");
        String input = SCANNER.nextLine();
        while (!Objects.equals(input, "Quit")) {
            this.eval(input);
            input = SCANNER.nextLine();
        }
    }

    public void reload () {
        try {
            ChessGame chessGame = new ChessGame();
            ChessBoard chessBoard = chessGame.getBoard();
            if (teamColor == ChessGame.TeamColor.WHITE) {
                BoardUI.callBlackTiles(OUT, chessBoard, null);
            } else if (teamColor == ChessGame.TeamColor.BLACK) {
                BoardUI.callWhiteTiles(OUT, chessBoard, null);
            }
        } catch (Exception e) {
            System.out.println("Error: failed to reload.");
        }
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
