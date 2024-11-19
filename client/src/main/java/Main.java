import chess.*;
import ui.PreloginUI;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        PreloginUI prelogin = new PreloginUI("http://localhost:8080");
        prelogin.run();
        System.out.println("♕ 240 Chess Client: " + piece);
    }
}