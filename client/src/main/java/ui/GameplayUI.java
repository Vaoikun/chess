package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;

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
    private WebSocketFacade webSocketFacade;

    public GameplayUI (String serverURL, String authToken, ChessGame.TeamColor teamColor, int gameID, WebSocketFacade webSocketFacade) {
        ServerFacade serverFacade = new ServerFacade(serverURL);
        this.authToken = authToken;
        this.teamColor = teamColor;
        this.gameID = gameID;
        this.webSocketFacade = webSocketFacade;
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
        try {
            OUT.println("Enter gameID below.");
            String enteredID = SCANNER.nextLine();
            int gameID = Integer.parseInt(enteredID);
            OUT.println("Are you sure you want to leave? (Yes/No)");
            String answer = SCANNER.nextLine();
            if (Objects.equals(answer, "Yes")) {
                webSocketFacade.leave(authToken, gameID);
                PostloginUI postloginUI = new PostloginUI("http://localhost:8080", authToken);
                postloginUI.run();
            } else {
                System.out.println("You are still in the game.");
            }
        } catch (Exception e) {
            System.out.println("Error: Failed to process.");
        }
    }

    public void resign () {
        Gson json = new Gson();
        try {
            OUT.println("Enter gameID below.");
            String enteredID = SCANNER.nextLine();
            int gameID = Integer.parseInt(enteredID);
            OUT.println("Are you sure you want to resign? (Yes/No)");
            String answer = SCANNER.nextLine();
            if (Objects.equals(answer, "Yes")) {
                webSocketFacade.resign(authToken, gameID);
                PostloginUI postloginUI = new PostloginUI("http://localhost:8080", authToken);
                postloginUI.run();
            } else {
                System.out.println("You are still in the game.");
            }
        } catch (Exception e) {
            System.out.println("Error: Failed to process.");
        }
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
