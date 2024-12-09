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
        OUT.println("Welcome to the game!");
        OUT.println();
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

    public void resign() {
        Gson gson = new Gson();
        try {
            OUT.println("Enter the gameID.");
            String gameIDStr = SCANNER.nextLine();
            int gameID = Integer.parseInt(gameIDStr);
            OUT.println("Are you sure you want to leave? YES / NO");
            String answer = SCANNER.nextLine();
            if (Objects.equals(answer, "YES")) {
                webSocketFacade.resign(authToken, PostloginUI.gamesNumber.get(gameID - 1));
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
            OUT.println("Enter the gameID");
            String gameIDStr = SCANNER.nextLine();
            int gameID = Integer.parseInt(gameIDStr);
            OUT.println("Are you sure you want to leave? YES / NO");
            String answer = SCANNER.nextLine();
            if (Objects.equals(answer, "YES")) {
                webSocketFacade.leave(authToken, PostloginUI.gamesNumber.get(gameID - 1));
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
            String pieceCoordinateStr = SCANNER.nextLine();
            ArrayList<Integer> pieceCoordinate = textConverter(pieceCoordinateStr);
            int row = pieceCoordinate.get(1);
            int col = pieceCoordinate.get(0);
            ChessPosition piecePosition = new ChessPosition(row, col);
            OUT.println("Select a position. (Enter a coordinate.)");
            String targetCoordinateStr = SCANNER.nextLine();
            ArrayList<Integer> targetCoordinate = textConverter(targetCoordinateStr);
            int rowCoord = targetCoordinate.get(1);
            int colCoord = targetCoordinate.get(0);
            ChessPosition targetPosition = new ChessPosition(rowCoord, colCoord);
            ChessGame chessGame = webSocketFacade.chessGame;
            ChessBoard chessBoard = chessGame.getBoard();
            ChessPiece movingPiece = chessBoard.getPiece(piecePosition);
            ChessPiece targetPiece = chessBoard.getPiece(targetPosition);
            ChessPiece.PieceType promoteType = null;
            try {
                if ((chessBoard.getPiece(piecePosition)).getPieceType() == ChessPiece.PieceType.PAWN && rowCoord == 8) {
                    OUT.println("Which piece type would you like to promote to? (e.g. QUEEN)");
                    String promoteTypeInput = SCANNER.nextLine();
                    promoteType = promoteTypeSetter(promoteTypeInput);
                }
            }catch (Exception e){
                System.out.println("Choose a valid piece type.");
            }
            ChessMove move = new ChessMove(piecePosition, targetPosition, promoteType);
            chessGame.makeMove(move);
            webSocketFacade.makeMove(authToken, gameID, move);
            redraw();
        }catch (Exception e) {
            System.out.println("Invalid positions.");
        }
    }

    public ChessPiece.PieceType promoteTypeSetter(String promoteType){
        promoteType = promoteType.toUpperCase();
        ChessPiece.PieceType properType = null;
        switch (promoteType){
            case "QUEEN" ->  properType = ChessPiece.PieceType.QUEEN;
            case "KING" ->  properType = ChessPiece.PieceType.KING;
            case "ROOK" ->  properType = ChessPiece.PieceType.ROOK;
            case "BISHOP" ->  properType = ChessPiece.PieceType.BISHOP;
            case "KNIGHT" ->  properType = ChessPiece.PieceType.KNIGHT;
            default -> properType = ChessPiece.PieceType.PAWN;
        }
        return properType;
    }

    public void highLight(){
        OUT.println("Which piece would you like to highlight the moves?");
        String coordinateStr = SCANNER.nextLine();
        ArrayList<Integer> coordinate = textConverter(coordinateStr);
        int row = coordinate.get(1);
        int col = coordinate.get(0);
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

    public static ArrayList<Integer> textConverter(String ab){
        Integer col = null;
        Integer row = null;
        ArrayList<Integer> coordinate = new ArrayList<>();
        try {
            row = Integer.parseInt(String.valueOf(ab.charAt(1)));
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid row number.");
        }
        try{
            String colLetter = String.valueOf(ab.charAt(0));
            switch (colLetter) {
                case "a" -> col = 1;
                case "b" -> col = 2;
                case "c" -> col = 3;
                case "d" -> col = 4;
                case "e" -> col = 5;
                case "f" -> col = 6;
                case "g" -> col = 7;
                case "h" -> col = 8;
                default -> col = null;
            }
        } catch (Exception e) {
            System.out.println("Please enter a valid column letter.");
        }
        coordinate.add(col);
        coordinate.add(row);
        return coordinate;
    }
}