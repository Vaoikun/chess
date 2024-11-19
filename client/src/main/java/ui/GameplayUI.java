package ui;

import chess.*;
import dataaccess.*;
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
        OUT.println(help());
        String input = SCANNER.nextLine();
        while (!Objects.equals(input, "Quit")) {
            this.eval(input);
            input = SCANNER.nextLine();
        }
    }

    public void eval(String input) {
        switch (input) {
            case "Redraw Chess Board" -> redraw();
//            case "Leave" -> leave();
//            case "Make a Move" -> makeMove();
            case "Resign" -> resign();
//            case "Highlight Legal Moves" ->highLight();
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

//    public void leave() {
//        // just in case
//        OUT.println(RESET_BG_COLOR);
//        OUT.println(RESET_TEXT_COLOR);
//
//        Gson gson = new Gson();
//        try {
//            OUT.println("Enter the gameID of the game you would like to leave.");
//            String gameIdStr = SCANNER.nextLine();
//            int gameID = Integer.parseInt(gameIdStr);
//            OUT.println("Are you sure you want to leave? YES / NO");
//            String answer = SCANNER.nextLine();
//            if (Objects.equals(answer, "YES")) {
//                webSocketFacade.leave(authToken, PostloginUI.gamesNumber.get(gameID - 1));
//                System.out.println(PostloginUI.gamesNumber);
//                PostloginUI postlogin = new PostloginUI("http://localhost:8080", authToken);
//                postlogin.run();
//            } else {
//                System.out.println("You are still in the game.");
//            }
//        } catch (Exception E) {
//            System.out.println(E.getMessage());
//        }
//    }

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
            SQLAuthDAO sqlAuth = new SQLAuthDAO();
//            ChessGame chessGameRecord = webSocketFacade.chessGame;
//            ChessBoard chessBoard = chessGameRecord.getBoard();
            String username = sqlAuth.getAuth(this.authToken);
            ChessGame chessGame1 = new ChessGame();
            ChessBoard chessBoard = chessGame1.getBoard();
            if (color == ChessGame.TeamColor.BLACK) {
                BoardUI.callWhiteBoard(OUT, chessBoard, null);
            } else if (color == ChessGame.TeamColor.WHITE) {
                BoardUI.callWhiteBoard(OUT, chessBoard, null);
            } else {
                BoardUI.callWhiteBoard(OUT, chessBoard, null);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

//    public  void makeMove() {
//        try {
//            OUT.println("Please tell me which position you want to make ?");
//            int row = SCANNER.nextInt();
//            int column = SCANNER.nextInt();
//            ChessPosition chessPosition = new ChessPosition(row, column);
//            OUT.println("Please tell me which position you want to go ?");
//            int rowDes = SCANNER.nextInt();
//            int colDes = SCANNER.nextInt();
//            ChessPosition chessPositionDes = new ChessPosition(rowDes, colDes);
//            ChessGame chessGameRecord = webSocketFacade.chessGame;
//            ChessBoard board = chessGameRecord.getBoard();
//            ChessPiece targetPiece = board.getPiece(chessPositionDes);
//            ChessPiece startPiece = board.getPiece(chessPosition);
////        if (startPiece.getPieceType() == ChessPiece.PieceType.PAWN)
////        {
////            if (chessPositionDes.getRow() == 7 || chessPositionDes.getRow() == 8) // pawn promote // do we need to care about the promote situation?
////            {
////                ChessMove theMove = new ChessMove(chessPosition, chessPositionDes, );
////            }
////        }
//            ChessMove theMove = new ChessMove(chessPosition, chessPositionDes, null);
//            chessGameRecord.makeMove(theMove);
//            webSocketFacade.makeMove(authToken, gameID, theMove);
//        } catch (Exception e) {
//            OUT.println(e.getMessage());
//        }
//    }
//
//    public void highLight() {
//        OUT.println("Please tell me which piece you would like to move.");
//        int row = SCANNER.nextInt(); // if the perspective changes, the row and column are not the same.
//        int column = SCANNER.nextInt();
//        ChessPosition chessPosition = new ChessPosition(row, column);
//        ChessGame chessGameRecord = webSocketFacade.chessGame;
//        ChessBoard board = chessGameRecord.getBoard();
//        ChessPiece targetPiece = board.getPiece(chessPosition);
//        Collection<ChessMove> potentialMoves = chessGameRecord.validMoves(chessPosition);
//        if (color == ChessGame.TeamColor.BLACK) {
//            BoardUI.callBlackBoard(OUT, board, potentialMoves);
//        } else if (color == ChessGame.TeamColor.WHITE) {
//            BoardUI.callWhiteBoard(OUT, board, potentialMoves);
//        } else {
//            BoardUI.callWhiteBoard(OUT, board, potentialMoves);
//        }
//    }
}