package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamColor;
    private ChessBoard board = new ChessBoard();

    //default initiation
    public ChessGame() {
        this.teamColor = TeamColor.WHITE;
        this.board.resetBoard();
    }

    //update
    public ChessGame(ChessGame.TeamColor newTeamColor, ChessBoard newBoard){
        this.teamColor = newTeamColor;
        this.board = newBoard;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currentPiece = this.board.getPiece(startPosition);
        if (currentPiece == null) {
            return null;
        }
        Collection<ChessMove> moveOptions = currentPiece.pieceMoves(this.board, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();

        for (ChessMove move: moveOptions) {
            ChessBoard newBoard = this.board.copyBoard();
            newBoard.addPiece(move.getStartPosition(), null);
            newBoard.addPiece(move.getStartPosition(), currentPiece);
            ChessGame upDatedGame = new ChessGame(this.teamColor, newBoard);

            if (!upDatedGame.isInCheck(currentPiece.getTeamColor())){
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        Collection<ChessMove> legalMoves = this.validMoves(startPosition);
        ChessPiece currentPiece = this.board.getPiece(startPosition);
        if (legalMoves == null) {
            throw new InvalidMoveException("currrentPiece is null");
        }
        if (legalMoves.contains(move)) {
            moveMaker(move, startPosition, endPosition, currentPiece);
            changeTurn();
        } else {
            throw new InvalidMoveException("Illegal moves.");
        }
    }

    public void changeTurn(){
        if (this.teamColor == TeamColor.BLACK){
            this.teamColor = TeamColor.WHITE;
        } else {
            this.teamColor = TeamColor.BLACK;
        }
    }

    public void moveMaker(ChessMove move, ChessPosition startPosition, ChessPosition endPosition, ChessPiece currentPiece) {
        this.board.addPiece(startPosition, null);
        if (move.getPromotionPiece() == null){
            this.board.addPiece(endPosition, currentPiece);
        } else {
            ChessPiece newPiece = new ChessPiece(this.teamColor, move.getPromotionPiece());
            this.board.addPiece(endPosition, newPiece);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check　
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> legalMoves = new ArrayList<>();
        ChessPosition kingsPosition = kingsPosition(teamColor);
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                ChessPosition currentPosition = new ChessPosition(row + 1, col + 1);
                ChessPiece currentPiece = this.board.getPiece(currentPosition);
                if (currentPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> opponentMoves = currentPiece.pieceMoves(this.board, currentPosition);
                    return checkMove(opponentMoves, kingsPosition);
                }
            }
        }
        return false;
    }

    public boolean checkMove (Collection<ChessMove> moves, ChessPosition kingsPosition) {
        for (ChessMove move : moves) {
            if (move.getEndPosition() == kingsPosition){
                return true;
            }
        }
        return false;
    }

    public ChessPosition kingsPosition (TeamColor teamColor) {
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                ChessPosition currentPosition = new ChessPosition(row + 1, col + 1);
                ChessPiece currentPiece = this.board.getPiece(currentPosition);
                if (currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == teamColor) {
                    return currentPosition;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> legalMoves = new ArrayList<>();
        if (!isInCheck(teamColor)){
            return false;
        }
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row + 1, col + 1);
                ChessPiece currentPiece = this.board.getPiece(currentPosition);
                legalMoves = validMoves(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor && !legalMoves.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamColor == chessGame.teamColor && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, board);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamColor=" + teamColor +
                ", board=" + board +
                '}';
    }
}
