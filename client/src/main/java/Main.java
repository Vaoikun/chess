import chess.*;
import ui.Prelogin;

public class Main {


    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        Prelogin prelogin = new Prelogin("http://localhost:8080");
        prelogin.run();
        System.out.println("♕ 240 Chess Client: " + piece);
    }
}