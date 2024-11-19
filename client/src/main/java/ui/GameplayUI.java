package ui;

import chess.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintStream;
import java.net.http.WebSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayUI
{

    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private static final Scanner SCANNER = new Scanner(System.in);

    private PostloginUI postLogin;


    public ChessGame chessGame = null;
    private String authToken;

    private ChessGame.TeamColor color;

    private int gameID;
    public GameplayUI(String serverUrl, String authToken, ChessGame.TeamColor color, int gameID) {
        ServerFacade serverfacade = new ServerFacade(serverUrl);
        this.authToken = authToken;
        this.color = color;
        this.gameID = gameID;

    }

    public void run() {
        OUT.println();
        OUT.println("Welcome to your game!");
        OUT.println();
        redraw();
        OUT.println();
        OUT.println("What would you like to do?");
        String input = SCANNER.nextLine();
        while (!Objects.equals(input, "Quit")) {
            this.eval(input);
            input = SCANNER.nextLine();
        }
    }

    public void eval(String input) {
        switch (input) {
            case "Redraw Chess Board" -> redraw();
            case "Resign" -> resign();
            case "Help" -> OUT.println(help());
            default -> OUT.println(help());
        }
    }

    public static String help() {
        return """
               Redraw Chess Board -- (out of service)Redraws the chess board.
               Leave -- (out of service)Removes the user from the game. You will be sent back to the Post-Login page.
               Make a Move -- (out of service)Select a piece and type in the destination coordinates.
               Resign -- Resign from the game.
               Highlight Legal Moves -- (out of service)Highlights the legal moves in a chess board.
               Help - With possible commands.
               """;
    }

    public  void resign() {
        Gson gson = new Gson();
        try {
            OUT.print("Which game you would like to resign from?");
            String gameIDStr = SCANNER.nextLine();
            int gameID = Integer.parseInt(gameIDStr);
            OUT.println("Are you sure you want to leave? YES / NO");
            String answer = SCANNER.nextLine();
            if (Objects.equals(answer, "YES")) {
                System.out.println(PostloginUI.gamesNumber);
                PostloginUI postlogin = new PostloginUI("http://localhost:8080", authToken);
                postlogin.run();
            } else {
                System.out.println("You are still in the game.");
            }
        } catch (Exception E) {
            System.out.println(E.getMessage());
        }
    }

    public void redraw() {
        try {
            ChessGame chessGame1 = new ChessGame();
            ChessBoard chessBoard = chessGame1.getBoard();
            if (color == ChessGame.TeamColor.BLACK) {
                BoardUI.callBlackBoard(OUT, chessBoard, null);
            } else if (color == ChessGame.TeamColor.WHITE) {
                BoardUI.callWhiteBoard(OUT, chessBoard, null);
            } else {
                BoardUI.callWhiteBoard(OUT, chessBoard, null);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}