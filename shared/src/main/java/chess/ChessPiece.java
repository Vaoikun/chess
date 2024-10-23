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
public class ChessPiece implements Cloneable{

    private ChessGame.TeamColor pieceColor;
    private PieceType type;

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

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> thatPieceMoves = new ArrayList<>();
        // get the piece
        ChessPiece currentPiece = board.getPiece(myPosition);
        switch (currentPiece.type)
        {
            case BISHOP:
                BishopMovesCalculator bishopMoves = new BishopMovesCalculator();
                thatPieceMoves.addAll(bishopMoves.pieceMoves(board, myPosition)); // addAll means add all elements in a collection into another one.
                break;
            case ROOK:
                RookMovesCalculator rookMoves = new RookMovesCalculator();
                thatPieceMoves.addAll(rookMoves.pieceMoves(board, myPosition));
                break;
            case KING, QUEEN:
                KingMovesCalculator kingMoves = new KingMovesCalculator();
                thatPieceMoves.addAll(kingMoves.pieceMoves(board, myPosition));
                break;
            case KNIGHT:
                KnightMovesCalculator knightMoves = new KnightMovesCalculator();
                thatPieceMoves.addAll(knightMoves.pieceMoves(board, myPosition));
                break;
            case PAWN:
                PawnMovesCalculator pawnMoves = new PawnMovesCalculator();
                thatPieceMoves.addAll(pawnMoves.pieceMoves(board, myPosition));
                break;


        }
        return thatPieceMoves;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        Object o = super.clone();
        return new ChessPiece(this.pieceColor, this.type);
    }


}
