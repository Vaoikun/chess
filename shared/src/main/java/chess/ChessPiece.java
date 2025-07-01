package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece currentPiece = board.getPiece(myPosition);
        PieceType currentPieceType = currentPiece.getPieceType();
        ChessGame.TeamColor currentTeamColor = currentPiece.getTeamColor();
        List<ChessMove> moves = new ArrayList<>();
        int col = myPosition.getColumn();
        int row = myPosition.getRow();

        switch (currentPieceType){
            case BISHOP:

            case KING:

            case KNIGHT:

            case PAWN:

            case QUEEN:

            case ROOK:

            default:
                throw new IllegalArgumentException("Unexpected piece type: " + currentPieceType.toString());
        }

    }

    public static List<ChessMove> diagonal(ChessBoard board, int row, int col, ChessGame.TeamColor teamColor, PieceType type){
        ChessPosition startPosition = new ChessPosition(row, col);
        List<ChessMove> legalMoves = new ArrayList<>();

        // Right up
        for (int i = row + 1, j = col + 1; i <= 8 && j <= 8; i++, j++){
            if (legalityCheck(board, teamColor, startPosition, legalMoves, i, j)){
                break;
            }
            if (type == PieceType.KING) {
                break;
            }
        }

        // Right down
        for (int i = row - 1, j = col + 1; i >= 1 && j <= 8; i--, j++){
            if (legalityCheck(board, teamColor, startPosition, legalMoves, i, j)){
                break;
            }
            if (type == PieceType.KING) {
                break;
            }
        }

        // Left up
        for (int i = row + 1, j = col - 1; i <= 8 && j >= 1; i++, j--){
            if (legalityCheck(board, teamColor, startPosition, legalMoves, i, j)){
                break;
            }
            if (type == PieceType.KING) {
                break;
            }
        }

        // Left down
        for (int i = row - 1, j = col - 1; i >= 1 && j >= 1; i--, j--){
            if (legalityCheck(board, teamColor, startPosition, legalMoves, i, j)){
                break;
            }
            if (type == PieceType.KING) {
                break;
            }
        }
        return legalMoves;
    }

    public static List<ChessMove> straightLine(ChessBoard board, int row, int col, ChessGame.TeamColor teamColor, PieceType type) {
        ChessPosition startPosition = new ChessPosition(row, col);
        List<ChessMove> legalMoves = new ArrayList<>();

        // Up
        for (int i = row + 1; i <= 8; i++) {
            if (legalityCheck(board, teamColor, startPosition, legalMoves, i, col)) {
                break;
            }
            if (type == PieceType.KING) {
                break;
            }
        }

        // Down
        for (int i = row - 1; i >= 1; i--) {
            if (legalityCheck(board, teamColor, startPosition, legalMoves, i, col)) {
                break;
            }
            if (type == PieceType.KING) {
                break;
            }
        }

        // Right
        for (int j = col + 1; j <= 8; j++) {
            if (legalityCheck(board, teamColor, startPosition, legalMoves, row, j)) {
                break;
            }
            if (type == PieceType.KING) {
                break;
            }
        }

        // Left
        for (int j = col - 1; j >= 1; j--) {
            if (legalityCheck(board, teamColor, startPosition, legalMoves, row, j)) {
                break;
            }
            if (type == PieceType.KING) {
                break;
            }
        }

        return legalMoves;

    }

    private static boolean legalityCheck(ChessBoard board, ChessGame.TeamColor teamColor, ChessPosition startPosition,
                                         List<ChessMove> legalMoves, int row, int col) {
        ChessPiece currentPiece = board.getPiece(new ChessPosition(row, col));
        if (currentPiece != null) {
            if (currentPiece.pieceColor == teamColor) {
                return true;
            }
            legalMoves.add(new ChessMove(startPosition, new ChessPosition(row, col), null));
            return true;
        }
        legalMoves.add(new ChessMove(startPosition, new ChessPosition(row, col), null));
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
