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

public class GameplayUI {

    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private static final Scanner SCANNER = new Scanner(System.in);

    private WebSocketFacade webSocketFacade;

    private PostloginUI postLogin;

    public ChessGame chessGame = null;

    private String authToken;

    private ChessGame.TeamColor color;

    private int gameID;
    public GameplayUI(String serverUrl, String authToken, WebSocketFacade webSocketFacade, ChessGame.TeamColor color, int gameID) {
        ServerFacade serverfacade = new ServerFacade(serverUrl);
        this.authToken = authToken;
        this.color = color;
        this.gameID = gameID;
        this.webSocketFacade = webSocketFacade;
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
            case "Make a Move" -> makeMove();
            case "Highlight Legal Moves" -> highLight();
            case "Leave" -> leave();
            case "Resign" -> resign();
            case "Help" -> OUT.println(help());
            default -> OUT.println(help());
        }
    }

    public static String help() {
        return """
               Redraw Chess Board -- Redraws the chess board.
               Make a Move -- Select a piece and type in the destination coordinates.
               Highlight Legal Moves -- Highlights the legal moves in a chess board.
               Leave -- Removes the user from the game. You will be sent back to the Post-Login page.
               Resign -- Resign from the game.
               Help - With possible commands.
               """;
    }

    public  void resign() {
        Gson gson = new Gson();
        try {
            OUT.print("Which game you would like to resign?");
            String gameIDStr = SCANNER.nextLine();
            int gameID = Integer.parseInt(gameIDStr);
            OUT.println("Are you sure you want to leave? YES / NO");
            String answer = SCANNER.nextLine();
            if (Objects.equals(answer, "YES")) {
                webSocketFacade.resign(authToken, PostloginUI.gamesNumber.get(gameID - 1));
                System.out.println(PostloginUI.gamesNumber);
                PostloginUI postlogin = new PostloginUI("http://localhost:8080", authToken);
                postlogin.run();
            } else {
                System.out.println("You are still in the game.");
            }
        } catch (Exception E) {
            System.out.println("Process failure. You are still in the game.");
        }
    }

    public void leave() {
        try {
            OUT.println("Which game you would like to leave?");
            String gameIDStr = SCANNER.nextLine();
            int gameID = Integer.parseInt(gameIDStr);
            OUT.println("Are you sure you want to leave? YES / NO");
            String answer = SCANNER.nextLine();
            if (Objects.equals(answer, "YES")) {
                webSocketFacade.leave(authToken, PostloginUI.gamesNumber.get(gameID - 1));
                System.out.println(PostloginUI.gamesNumber);
                PostloginUI postlogin = new PostloginUI("http://localhost:8080", authToken);
                postlogin.run();
            } else {
                System.out.println("You are still in the game.");
            }
        } catch (Exception E) {
            System.out.println("Process failure. You are still in the game.");
        }
    }

    public void redraw() {
        try {
            ChessGame chessGame = webSocketFacade.chessGame;
            ChessBoard chessBoard = chessGame.getBoard();
            if (color == ChessGame.TeamColor.BLACK) {
                BoardUI.callBlackBoard(OUT, chessBoard, null);
            } else if (color == ChessGame.TeamColor.WHITE) {
                BoardUI.callWhiteBoard(OUT, chessBoard, null);
            } else {
                BoardUI.callWhiteBoard(OUT, chessBoard, null);
            }
        } catch (Exception e) {
            System.out.println("Failed to redraw chess board.");
        }
    }

    public void makeMove(){
        try{
            OUT.println("Which piece would you like to make move? (Enter a coordinate.)");
            int row = SCANNER.nextInt();
            int col = SCANNER.nextInt();
            ChessPosition piecePosition = new ChessPosition(row, col);
            OUT.println("Select a position. (Enter a coordinate.)");
            int rowCoord = SCANNER.nextInt();
            int colCoord = SCANNER.nextInt();
            ChessPosition targetPosition = new ChessPosition(rowCoord, colCoord);
            ChessGame chessGame = webSocketFacade.chessGame;
            ChessBoard chessBoard = chessGame.getBoard();
            ChessPiece movingPiece = chessBoard.getPiece(piecePosition);
            ChessPiece targetPiece = chessBoard.getPiece(targetPosition);
            ChessMove move = new ChessMove(piecePosition, targetPosition, null);
            chessGame.makeMove(move);
            webSocketFacade.makeMove(authToken, gameID, move);
        }catch (Exception e) {
            System.out.println("Invalid positions.");
        }
    }

    public void highLight(){
        OUT.println("Which piece would you like to highlight the moves?");
        int row = SCANNER.nextInt();
        int col = SCANNER.nextInt();
        ChessPosition piecePosition = new ChessPosition(row, col);
        ChessGame chessGame = webSocketFacade.chessGame;
        ChessBoard chessBoard = chessGame.getBoard();
        ChessPiece movingPiece = chessBoard.getPiece(piecePosition);
        Collection<ChessMove> possibleMoves = chessGame.validMoves(piecePosition);
        if (color == ChessGame.TeamColor.BLACK) {
            BoardUI.callBlackBoard(OUT, chessBoard, possibleMoves);
        }else if (color == ChessGame.TeamColor.WHITE) {
            BoardUI.callWhiteBoard(OUT, chessBoard, possibleMoves);
        }else{ // as an Observer
            BoardUI.callWhiteBoard(OUT, chessBoard, possibleMoves);
        }
    }
}