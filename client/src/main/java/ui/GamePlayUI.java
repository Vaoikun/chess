package ui;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SQLAuth;
import dataaccess.SQLGame;
import model.GameData;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GamePlayUI
{

    private static final PrintStream OUT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    private static final Scanner SCANNER = new Scanner(System.in);

    private final WebSocketFacade webSocketFacade;

    private PostLogin postLogin;


    public ChessGame chessGame = null;
    private String authToken;

    private ChessGame.TeamColor color;

    private int gameID;
    public GamePlayUI(String serverUrl, String authToken, WebSocketFacade webSocketFacade, ChessGame.TeamColor color, int gameID)
    {
        ServerFacade serverfacade = new ServerFacade(serverUrl);
        this.authToken = authToken;
        this.webSocketFacade = webSocketFacade;
        this.color = color;
        this.gameID = gameID;

    }

    public void run()
    {
        OUT.println();
        OUT.println("Welcome to your chess game. Please make your choice and enjoy the game.");

        OUT.println();
        OUT.println(help());
        String input = SCANNER.nextLine();
        while (!Objects.equals(input, "Quit"))
        {
            this.eval(input);
            input = SCANNER.nextLine();
        }
    }

    public void eval(String input)
    {
        switch (input)
        {
            case "Redraw Chess Board" -> redraw();
            case "Leave" -> leave();
            case "Make Move" -> makeMove();
            case "Resign" -> resign();
            case "Highlight Legal Moves" ->highLight();
            case "Help" -> OUT.println(help());
            default -> OUT.println(help());
        }
    }

    public static String help()
    {
        return """
               Redraw Chess Board -- Redraws the chess board upon the user’s request.
               Leave -- Removes the user from the game. The client transitions back to the Post-Login UI.
               Make Move -- Allow the user to input what move they want to make to make board update.
               Resign -- Resign the game.
               Highlight Legal Moves -- Allows the user to input the piece for which they want to highlight legal moves.
               Help - With possible commands.
                """;
    }

    public void leave() {
        // just in case
        OUT.println(RESET_BG_COLOR);
        OUT.println(RESET_TEXT_COLOR);

        Gson gson = new Gson();
        try
        {
            OUT.println("Please tell me which game you would like to leave.");
            String gameIdStr = SCANNER.nextLine();
            int gameID = Integer.parseInt(gameIdStr);
            OUT.println("Are you sure you want to leave? YES / NO");
            String answer = SCANNER.nextLine();
            if (Objects.equals(answer, "YES"))
            {
                webSocketFacade.leave(authToken, PostLogin.gamesNumber.get(gameID - 1));
                System.out.println(PostLogin.gamesNumber);
                PostLogin postlogin = new PostLogin("http://localhost:8080", authToken);
                postlogin.run();
            }
            else
            {
                System.out.println("You are still in the game.");
            }
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
        }

    }

    public  void resign()
    {
        Gson gson = new Gson();
        try
        {
            OUT.print("Please tell me which game you would like to resign?");
            String gameIDStr = SCANNER.nextLine();
            int gameID = Integer.parseInt(gameIDStr);
            OUT.println("Are you sure you want to leave? YES / NO");
            String answer = SCANNER.nextLine();
            if (Objects.equals(answer, "YES"))
            {
                webSocketFacade.resign(authToken, PostLogin.gamesNumber.get(gameID - 1));
            }
            else
            {
                System.out.println("You are still in the game.");
            }
        }
        catch (Exception E)
        {
            System.out.println(E.getMessage());
        }
    }

    public void redraw()
    {
        try
        {
            SQLAuth sqlAuth = new SQLAuth();
            ChessGame chessGameRecord = webSocketFacade.chessGame;
            ChessBoard chessBoard = chessGameRecord.getBoard();
            String username = sqlAuth.getAuth(this.authToken);
            if (color == ChessGame.TeamColor.BLACK)
            {
                BoardUI.callWhiteBoard(OUT, chessBoard, null);
            }
            else if (color == ChessGame.TeamColor.WHITE)
            {
                BoardUI.callWhiteBoard(OUT, chessBoard, null);
            }
            else // Observer
            {
                BoardUI.callWhiteBoard(OUT, chessBoard, null);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public  void makeMove()
    {
        try
        {
            OUT.println("Please tell me which position you want to make ?");
            int row = SCANNER.nextInt();
            int column = SCANNER.nextInt();
            ChessPosition chessPosition = new ChessPosition(row, column);
            OUT.println("Please tell me which position you want to go ?");
            int rowDes = SCANNER.nextInt();
            int colDes = SCANNER.nextInt();
            ChessPosition chessPositionDes = new ChessPosition(rowDes, colDes);
            ChessGame chessGameRecord = webSocketFacade.chessGame;
            ChessBoard board = chessGameRecord.getBoard();
            ChessPiece targetPiece = board.getPiece(chessPositionDes);
            ChessPiece startPiece = board.getPiece(chessPosition);
//        if (startPiece.getPieceType() == ChessPiece.PieceType.PAWN)
//        {
//            if (chessPositionDes.getRow() == 7 || chessPositionDes.getRow() == 8) // pawn promote // do we need to care about the promote situation?
//            {
//                ChessMove theMove = new ChessMove(chessPosition, chessPositionDes, );
//            }
//        }
            ChessMove theMove = new ChessMove(chessPosition, chessPositionDes, null);
            chessGameRecord.makeMove(theMove);

            webSocketFacade.makeMove(authToken, gameID, theMove);
        }
        catch (Exception e)
        {
            OUT.println(e.getMessage());
        }



    }

    public void highLight()
    {
        OUT.println("Please tell me which piece you would like to move.");
        int row = SCANNER.nextInt(); // if the perspective changes, the row and column are not the same.
        int column = SCANNER.nextInt();
        ChessPosition chessPosition = new ChessPosition(row, column);
        ChessGame chessGameRecord = webSocketFacade.chessGame;
        ChessBoard board = chessGameRecord.getBoard();
        ChessPiece targetPiece = board.getPiece(chessPosition);
        Collection<ChessMove> potentialMoves = chessGameRecord.validMoves(chessPosition);
        if (color == ChessGame.TeamColor.BLACK)
        {
            BoardUI.callBlackBoard(OUT, board, potentialMoves);
        }
        else if (color == ChessGame.TeamColor.WHITE)
        {
            BoardUI.callWhiteBoard(OUT, board, potentialMoves);
        }
        else // Observer
        {
            BoardUI.callWhiteBoard(OUT, board, potentialMoves);
        }

    }
}
