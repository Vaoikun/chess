package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator
{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        ChessPiece thePiece = board.getPiece(myPosition);
        return pawnMoves(board, thePiece, myPosition);
    }

    @Override
    public String toString() {
        return "PawnMovesCalculator{}";
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPiece thePiece, ChessPosition startPosition) {
        ArrayList<ChessMove> pawnMoves = new ArrayList<>();
        int startRow = startPosition.getRow();
        int startColumn = startPosition.getColumn();
        int nextRow, setUpRow;

        if (thePiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            setUpRow = 2;
            nextRow = startRow + 1;
        } else {
            setUpRow = 7;
            nextRow = startRow - 1;
        }

        addForwardMoves(board, thePiece, startPosition, startRow, startColumn, nextRow, setUpRow, pawnMoves);
        addCaptureMoves(board, thePiece, startPosition, startRow, startColumn, nextRow, pawnMoves);

        return pawnMoves;
    }

    private void addForwardMoves(ChessBoard board, ChessPiece thePiece, ChessPosition startPosition, int startRow, int startColumn, int nextRow, int setUpRow, ArrayList<ChessMove> pawnMoves) {
        if (rowInBound(nextRow) && board.getPiece(new ChessPosition(nextRow, startColumn)) == null) {
            if (nextRow == 1 || nextRow == 8) {
                addPromotionMoves(startPosition, nextRow, startColumn, pawnMoves);
            } else {
                pawnMoves.add(new ChessMove(startPosition, new ChessPosition(nextRow, startColumn), null));
            }

            if (startRow == setUpRow) {
                int nextNextRow = thePiece.getTeamColor() == ChessGame.TeamColor.WHITE ? nextRow + 1 : nextRow - 1;
                if (rowInBound(nextNextRow) && board.getPiece(new ChessPosition(nextNextRow, startColumn)) == null) {
                    pawnMoves.add(new ChessMove(startPosition, new ChessPosition(nextNextRow, startColumn), null));
                }
            }
        }
    }

    private void addCaptureMoves(ChessBoard board, ChessPiece thePiece, ChessPosition startPosition, int startRow, int startColumn, int nextRow, ArrayList<ChessMove> pawnMoves) {
        int[] captureColumns = {startColumn + 1, startColumn - 1};
        for (int captureColumn : captureColumns) {
            ChessPosition capturePosition = new ChessPosition(nextRow, captureColumn);
            if (rowInBound(captureColumn) && board.getPiece(capturePosition) != null && board.getPiece(capturePosition).getTeamColor() != thePiece.getTeamColor()) {
                if (nextRow == 1 || nextRow == 8) {
                    addPromotionMoves(startPosition, nextRow, captureColumn, pawnMoves);
                } else {
                    pawnMoves.add(new ChessMove(startPosition, capturePosition, null));
                }
            }
        }
    }

    private void addPromotionMoves(ChessPosition startPosition, int row, int column, ArrayList<ChessMove> pawnMoves) {
        ChessPiece.PieceType[] promotionPieces = {
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.QUEEN
        };
        for (ChessPiece.PieceType pieceType : promotionPieces) {
            pawnMoves.add(new ChessMove(startPosition, new ChessPosition(row, column), pieceType));
        }
    }

    public boolean rowInBound(int row)
    {
        return row >= 1 && row <= 8;
    }
}
