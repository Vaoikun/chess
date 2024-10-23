package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMovesCalculator {
    default Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {

        return null;
    }
}
