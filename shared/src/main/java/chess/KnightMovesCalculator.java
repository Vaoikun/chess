package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator
{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        ChessPiece thePiece = board.getPiece(myPosition);
        return knightMove(board, thePiece, myPosition);
    }

    private void addingToMoves(int nextRow, int nextColumn, ChessPosition startPosition, ChessBoard board, ArrayList<ChessMove> knightMoves, ChessPiece thePiece)
    {
        if (isInBound(new ChessPosition(nextRow, nextColumn)))
        {
            ChessPosition endPosition = new ChessPosition(nextRow, nextColumn); // get the end Position
            ChessPiece endPiece = board.getPiece(endPosition); // get the piece
            if (endPiece == null)
            {
                ChessMove smallMove = new ChessMove(startPosition, endPosition, null);
                knightMoves.add(smallMove);
            }
            else
            {
                if (endPiece.getTeamColor() != thePiece.getTeamColor())
                {
                    ChessMove smallMove = new ChessMove(startPosition, endPosition, null);
                    knightMoves.add(smallMove);
                }
            }
        }


    }
    private Collection<ChessMove> knightMove(ChessBoard board, ChessPiece thePiece, ChessPosition startPosition) {
        int startColumn = startPosition.getColumn();
        int startRow = startPosition.getRow();
        int nextRow;
        int nextColumn;

        ArrayList<ChessMove> knightMoves = new ArrayList<>();
        // right 1 up 2
        nextRow = startRow + 2;
        nextColumn = startColumn + 1;
        addingToMoves(nextRow, nextColumn, startPosition, board, knightMoves, thePiece);



        // left 1 up 2
        nextColumn = startColumn - 1;
        nextRow = startRow + 2;
        addingToMoves(nextRow, nextColumn, startPosition, board, knightMoves, thePiece);


        // right 2 up 1
        nextColumn = startColumn + 2;
        nextRow = startRow + 1;
        addingToMoves(nextRow, nextColumn, startPosition, board, knightMoves, thePiece);


        // left 2 up 1
        nextColumn = startColumn - 2;
        nextRow = startRow + 1;
        addingToMoves(nextRow, nextColumn, startPosition, board, knightMoves, thePiece);

        // right 1 down 2
        nextColumn = startColumn + 1;
        nextRow = startRow - 2;
        addingToMoves(nextRow, nextColumn, startPosition, board, knightMoves, thePiece);

        // left 1 down 2
        nextColumn = startColumn - 1;
        nextRow = startRow - 2;
        addingToMoves(nextRow, nextColumn, startPosition, board, knightMoves, thePiece);

        // right 2 down 1
        nextColumn = startColumn + 2;
        nextRow = startRow - 1;
        addingToMoves(nextRow, nextColumn, startPosition, board, knightMoves, thePiece);

        // left 2 down 1
        nextColumn = startColumn - 2;
        nextRow = startRow - 1;
        addingToMoves(nextRow, nextColumn, startPosition, board, knightMoves, thePiece);

        return knightMoves;

    }

    public static boolean isInBound(ChessPosition currentPosition)
    {
        return currentPosition.getColumn() >= 1 && currentPosition.getColumn() <= 8 && currentPosition.getRow() >= 1 && currentPosition.getRow() <= 8;
    }

}
