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

    private ChessGame.TeamColor teamColor;
    private ChessPiece.PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
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
        return this.teamColor;
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
            case KING:
                moves.addAll(straight(board, row, col, currentTeamColor, currentPieceType));
                moves.addAll(diagonal(board, row, col, currentTeamColor, currentPieceType));
                break;
            case QUEEN:
                moves.addAll(straight(board, row, col, currentTeamColor, currentPieceType));
                moves.addAll(diagonal(board, row, col, currentTeamColor, currentPieceType));
                break;
            case BISHOP:
                moves.addAll(diagonal(board, row, col, currentTeamColor, currentPieceType));
                break;
            case KNIGHT:
                moves.addAll(knightMoves(board, row, col, currentTeamColor, currentPieceType));
                break;
            case ROOK:
                moves.addAll(straight(board, row, col, currentTeamColor, currentPieceType));
                break;
            case PAWN:
                moves.addAll(pawnMoves(board, row, col, currentTeamColor, currentPieceType));
                break;

            default:
                throw new IllegalArgumentException("Unexpected piece type: " + currentPieceType.toString());
        }

        return moves;
    }

    public static List<ChessMove> straight(ChessBoard board, int row, int col, ChessGame.TeamColor currentTeamColor, PieceType currentPieceType) {
        ChessPosition startPosition = new ChessPosition(row, col);
        List<ChessMove> legalMoves = new ArrayList<>();

// Right
        for (int j = col + 1; j <= 8; j++) {
            if (addCheck(board, currentTeamColor, startPosition, legalMoves, row, j)) break;
            if(currentPieceType == PieceType.KING) break;
        }
// Left
        for (int j = col - 1; j >= 1; j--) {
            if (addCheck(board, currentTeamColor, startPosition, legalMoves, row, j)) break;
            if(currentPieceType == PieceType.KING) break;
        }
// Up
        for (int i = row + 1; i <= 8; i++) {
            if (addCheck(board, currentTeamColor, startPosition, legalMoves, i, col)) break;
            if(currentPieceType == PieceType.KING) break;
        }
// Down
        for (int i = row - 1; i >= 1; i--) {
            if (addCheck(board, currentTeamColor, startPosition, legalMoves, i, col)) break;
            if(currentPieceType == PieceType.KING) break;
        }
        return legalMoves;
    }

    public static List<ChessMove> diagonal(ChessBoard board, int row, int col, ChessGame.TeamColor currentTeamColor, PieceType currentPieceType) {
        ChessPosition startPosition = new ChessPosition(row, col);
        List<ChessMove> legalMoves = new ArrayList<>();
// Right down
        for (int i = row - 1, j = col + 1; i >= 1 && j <= 8; i--, j++) {
            if (addCheck(board, currentTeamColor, startPosition, legalMoves, i, j)) break;
            if(currentPieceType == PieceType.KING) break;
        }
// Right up
        for (int i = row + 1, j = col + 1; i <= 8 && j <= 8; i++, j++) {
            if (addCheck(board, currentTeamColor, startPosition, legalMoves, i, j)) break;
            if(currentPieceType == PieceType.KING) break;
        }

// Left down
        for (int i = row - 1, j = col - 1; i >= 1 && j >= 1; i--, j--) {
            if (addCheck(board, currentTeamColor, startPosition, legalMoves, i, j)) break;
            if(currentPieceType == PieceType.KING) break;
        }
// Left up
        for (int i = row + 1, j = col - 1; i <= 8 && j >= 1; i++, j--) {
            if (addCheck(board, currentTeamColor, startPosition, legalMoves, i, j)) break;
            if(currentPieceType == PieceType.KING) break;
        }

        return legalMoves;
    }

    public static List<ChessMove> knightMoves(ChessBoard board, int row, int col, ChessGame.TeamColor currentTeamColor, PieceType currentPieceType) {
        ChessPosition startPosition = new ChessPosition(row, col);
        List<ChessMove> legalMoves = new ArrayList<>();

        int nextRow;
        int nextCol;
    // Up 2 Right 1
        nextRow = row + 2;
        nextCol = col + 1;
        if(inbounds(nextRow, nextCol)){
            addCheck(board, currentTeamColor, startPosition, legalMoves, nextRow, nextCol);
        }
    // Up 2 Left 1
        nextRow = row + 2;
        nextCol = col - 1;
        if(inbounds(nextRow, nextCol)){
            addCheck(board, currentTeamColor, startPosition, legalMoves, nextRow, nextCol);
        }
    // Up 1 Right 2
        nextRow = row + 1;
        nextCol = col + 2;
        if(inbounds(nextRow, nextCol)){
            addCheck(board, currentTeamColor, startPosition, legalMoves, nextRow, nextCol);
        }
    // Up 1 Left 2
        nextRow = row + 1;
        nextCol = col - 2;
        if(inbounds(nextRow, nextCol)){
            addCheck(board, currentTeamColor, startPosition, legalMoves, nextRow, nextCol);
        }
    // Down 1 Right 2
        nextRow = row - 1;
        nextCol = col + 2;
        if(inbounds(nextRow, nextCol)){
            addCheck(board, currentTeamColor, startPosition, legalMoves, nextRow, nextCol);
        }
    // Down 1 Left 2
        nextRow = row - 1;
        nextCol = col - 2;
        if(inbounds(nextRow, nextCol)){
            addCheck(board, currentTeamColor, startPosition, legalMoves, nextRow, nextCol);
        }
    // Down 2 Right 1
        nextRow = row - 2;
        nextCol = col + 1;
        if(inbounds(nextRow, nextCol)){
            addCheck(board, currentTeamColor, startPosition, legalMoves, nextRow, nextCol);
        }
    // Down 2 Left 1
        nextRow = row - 2;
        nextCol = col - 1;
        if(inbounds(nextRow, nextCol)){
            addCheck(board, currentTeamColor, startPosition, legalMoves, nextRow, nextCol);
        }
        return legalMoves;
    }

    public static List<ChessMove> pawnMoves(ChessBoard board, int row, int col, ChessGame.TeamColor currentTeamColor, PieceType currentPieceType) {
        ChessPosition startPosition = new ChessPosition(row, col);
        List<ChessMove> legalMoves = new ArrayList<>();
        int startRow;
        int upnDown;
        if(currentTeamColor == ChessGame.TeamColor.WHITE){
            startRow = 2;
            upnDown = 1;
        }else{
            startRow = 7;
            upnDown = -1;
        }
        int nextRow = row + upnDown;
        boolean aheadEmpty = false;
        if(nextRow >= 1 && nextRow <= 8 && board.getPiece(new ChessPosition(nextRow, col)) == null){
            if(nextRow == 1 || nextRow == 8){
                legalMoves.add(new ChessMove(startPosition, new ChessPosition(nextRow, col), PieceType.ROOK));
                legalMoves.add(new ChessMove(startPosition, new ChessPosition(nextRow, col), PieceType.KNIGHT));
                legalMoves.add(new ChessMove(startPosition, new ChessPosition(nextRow, col), PieceType.BISHOP));
                legalMoves.add(new ChessMove(startPosition, new ChessPosition(nextRow, col), PieceType.QUEEN));
            }else{
                legalMoves.add(new ChessMove(startPosition, new ChessPosition(nextRow, col), null));
                aheadEmpty = true;
            }
        }

        //Checking diagonals
        //next(Up/Down) Right
        int nextCol = col + 1;
        ChessPiece observedPiece = null;
        pawnDiagonal(board, currentTeamColor, nextRow, nextCol, legalMoves, startPosition);
        //next(Up/Down) Left
        nextCol -= 2;
        pawnDiagonal(board, currentTeamColor, nextRow, nextCol, legalMoves, startPosition);
        //At the starting position
        if(startRow == row && aheadEmpty){
            //Check second row
            nextRow += upnDown;
            if(nextRow >= 1 && nextRow <= 8 && board.getPiece(new ChessPosition(nextRow, col)) == null){
                legalMoves.add(new ChessMove(startPosition, new ChessPosition(nextRow, col), null));
            }
        }
        return legalMoves;
    }

    private static void pawnDiagonal(ChessBoard board, ChessGame.TeamColor currentTeamColor, int nextRow, int nextCol, List<ChessMove> legalMoves, ChessPosition startPosition) {
        ChessPiece observedPiece;
        if(inbounds(nextRow, nextCol)){
            observedPiece = board.getPiece(new ChessPosition(nextRow, nextCol));
            //If there's a piece of an opposite side...
            if(observedPiece != null && observedPiece.getTeamColor() != currentTeamColor){
                //Promotion Time!
                if(nextRow == 1 || nextRow == 8){
                    legalMoves.add(new ChessMove(startPosition, new ChessPosition(nextRow, nextCol), PieceType.ROOK));
                    legalMoves.add(new ChessMove(startPosition, new ChessPosition(nextRow, nextCol), PieceType.KNIGHT));
                    legalMoves.add(new ChessMove(startPosition, new ChessPosition(nextRow, nextCol), PieceType.BISHOP));
                    legalMoves.add(new ChessMove(startPosition, new ChessPosition(nextRow, nextCol), PieceType.QUEEN));
                }else{
                    legalMoves.add(new ChessMove(startPosition, new ChessPosition(nextRow, nextCol), null));
                }
            }
        }
    }

    private static boolean addCheck(ChessBoard board, ChessGame.TeamColor currentTeamColor, ChessPosition startPosition, List<ChessMove> legalMoves, int i, int j) {
        ChessPiece observedPiece = board.getPiece(new ChessPosition(i, j));
        if (observedPiece != null) {
            if(observedPiece.teamColor == currentTeamColor) {
                return true;
            }
            legalMoves.add(new ChessMove(startPosition, new ChessPosition(i, j), null));
            return true;
        }
        legalMoves.add(new ChessMove(startPosition, new ChessPosition(i, j), null));
        return false;
    }

    private static boolean inbounds(int row, int col) {
        if(row <= 8 && col <= 8 && row >= 1 && col>=1){ return true;}
        else{ return false;}
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "teamColor=" + teamColor +
                ", type=" + type +
                '}';
    }
}
