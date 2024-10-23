package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator
{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        ArrayList<ChessMove> kingMoves = new ArrayList<>();
//        ChessPiece thePiece = board.getPiece(myPosition); // get the current piece
        RookMovesCalculator rookMoves = new RookMovesCalculator();
        BishopMovesCalculator bishopMoves = new BishopMovesCalculator();
        kingMoves.addAll(rookMoves.pieceMoves(board, myPosition));
        kingMoves.addAll(bishopMoves.pieceMoves(board, myPosition));
        return kingMoves;
    }


    @Override
    public String toString() {
        return "KingMovesCalculator{}";
    }
}
