package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator
{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        ArrayList<ChessMove> queenMoves = new ArrayList<>();
//        ChessPiece thePiece = board.getPiece(myPosition); // get the current piece
        RookMovesCalculator rookMoves = new RookMovesCalculator();
        BishopMovesCalculator bishopMoves = new BishopMovesCalculator();
        queenMoves.addAll(rookMoves.pieceMoves(board, myPosition));
        queenMoves.addAll(bishopMoves.pieceMoves(board, myPosition));
        return queenMoves;
    }
}
