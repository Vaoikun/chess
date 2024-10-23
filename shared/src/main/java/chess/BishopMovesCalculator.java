package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator
{

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece thePiece = board.getPiece(myPosition); // get the current piece

        return diagonal(board, thePiece, myPosition);
    }

    @Override
    public String toString() {
        return "BishopMovesCalculator{}";
    }

    public static Collection<ChessMove>  diagonal(ChessBoard board, ChessPiece thePiece, ChessPosition startPosition)
    {
        // get the start row and column
        int startColumn = startPosition.getColumn();
        int startRow = startPosition.getRow();

        ArrayList<ChessMove> allBishopMoves = new ArrayList<>();
        // up right
        for (int nextRow = startRow + 1, nextColumn = startColumn + 1; nextRow <= 8 && nextColumn <= 8; nextRow++, nextColumn++) {
           if (RookMovesCalculator.addingMove(board, thePiece, nextRow, nextColumn, allBishopMoves, startPosition))
           {
               break;
           }
            if(thePiece.getPieceType() == ChessPiece.PieceType.KING)
            {
                break; // because King can only go one step
            }

        }

        // up left
        for (int nextRow = startRow + 1, nextColumn = startColumn - 1; nextRow <= 8 && nextColumn >= 1; nextRow++, nextColumn--) {
            if (RookMovesCalculator.addingMove(board, thePiece, nextRow, nextColumn, allBishopMoves, startPosition))
            {
                break;
            }
            if(thePiece.getPieceType() == ChessPiece.PieceType.KING)
            {
                break; // because King can only go one step
            }
        }

        // down right
        for (int nextRow = startRow - 1, nextColumn = startColumn + 1; nextRow >= 1 && nextColumn <= 8; nextRow--, nextColumn++)
        {
            if (RookMovesCalculator.addingMove(board, thePiece, nextRow, nextColumn, allBishopMoves, startPosition))
            {
                break;
            }
            if(thePiece.getPieceType() == ChessPiece.PieceType.KING)
            {
                break; // because King can only go one step
            }

        }

        // down left
        for (int nextRow = startRow - 1, nextColumn = startColumn - 1; nextRow >= 1 && nextColumn >= 1; nextRow--, nextColumn--)
        {
            if (RookMovesCalculator.addingMove(board, thePiece, nextRow, nextColumn, allBishopMoves, startPosition))
            {
                break;
            }
            if(thePiece.getPieceType() == ChessPiece.PieceType.KING)
            {
                break; // because King can only go one step
            }
        }
        return allBishopMoves;
    }
}




