package ui;

import chess.*;
import com.google.gson.Gson;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
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
        try {
            OUT.println("Select a piece. (Enter coordinate.)");
            String input = SCANNER.nextLine();
            ArrayList<Integer> coordinate = coordinateConverter(input);
            int col = coordinate.get(0);
            int row = coordinate.get(1);
            ChessPosition chessPosition = new ChessPosition(row, col);
            OUT.println("Select an end position. (Enter coordinate.)");
            String endInput = SCANNER.nextLine();
            ArrayList<Integer> endCoordinate = coordinateConverter(endInput);
            int endCol = endCoordinate.get(0);
            int endRow = endCoordinate.get(1);
            ChessPosition endPosition = new ChessPosition(endRow, endCol);
            ChessGame chessGame = webSocketFacade.chessGame;
            ChessBoard chessBoard = chessGame.getBoard();
            ChessPiece movingPiece = chessBoard.getPiece(chessPosition);
            ChessPiece targetPiece = chessBoard.getPiece(endPosition);
            ChessPiece.PieceType promotionType = null;
            try {
                if ((chessBoard.getPiece(chessPosition)).getPieceType() == ChessPiece.PieceType.PAWN && endRow == 8) {
                    OUT.println("Enter a promotion type below. (e.g. QUEEN)");
                    String promotionInput = SCANNER.nextLine();
                    promotionType = promotion(promotionInput);
                }
            } catch (Exception e) {
                System.out.println("Error: Invalid piece type.");
            }
            ChessMove chessMove = new ChessMove(chessPosition, endPosition, promotionType);
            chessGame.makeMove(chessMove);
            webSocketFacade.makeMove(authToken, gameID, chessMove);
            reload();
        } catch (Exception e) {
            System.out.println("Error: Invalid piece position.");
        }
    }

    public ChessPiece.PieceType promotion (String promotionInput) {
        promotionInput = promotionInput.toUpperCase();
        ChessPiece.PieceType promotionType = null;
        switch (promotionInput) {
            case "QUEEN" -> promotionType = ChessPiece.PieceType.QUEEN;
            case "BISHOP" -> promotionType = ChessPiece.PieceType.BISHOP;
            case "ROOK" -> promotionType = ChessPiece.PieceType.ROOK;
            case "KNIGHT" -> promotionType = ChessPiece.PieceType.KNIGHT;
            default -> promotionType = ChessPiece.PieceType.PAWN;
        }
        return promotionType;
    }

    public void highlight () {
        OUT.println("Select a piece. (Enter coordinate.)");
        String piecePosition = SCANNER.nextLine();
        ArrayList<Integer> coordinate = coordinateConverter(piecePosition);
        int col = coordinate.get(0);
        int row = coordinate.get(1);
        ChessPosition chessPosition = new ChessPosition(row, col);
        ChessGame chessGame = webSocketFacade.chessGame;
        ChessBoard chessBoard = chessGame.getBoard();
        ChessPiece chessPiece = chessBoard.getPiece(chessPosition);
        Collection<ChessMove> legalMoves = chessGame.validMoves(chessPosition);
        if (teamColor == ChessGame.TeamColor.BLACK) {
            BoardUI.callBlackTiles(OUT, chessBoard, legalMoves);
        } else {
            BoardUI.callWhiteTiles(OUT, chessBoard, legalMoves);
        }
    }

    public static ArrayList<Integer> coordinateConverter (String input) {
        Integer col = null;
        Integer row = null;
        ArrayList<Integer> coordinate = new ArrayList<>();
        try {
            row = Integer.parseInt(String.valueOf(input.charAt(1)));
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid row number.");
        }
        try {
            String colInput = String.valueOf(input.charAt(2));
            switch (colInput) {
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
            System.out.println("Error: Invalid column letter.");
        }
        coordinate.add(col);
        coordinate.add(row);
        return coordinate;
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
