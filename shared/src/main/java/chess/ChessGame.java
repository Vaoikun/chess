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

    private ChessGame.TeamColor turnColor;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        this.turnColor = TeamColor.WHITE;
        this.board.resetBoard();
    }

    //for update
    public ChessGame(ChessGame.TeamColor turnColor, ChessBoard board) {
        this.turnColor = turnColor;
        this.board = board;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turnColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        //if (team == TeamColor.WHITE) {
           // this.turnColor = TeamColor.BLACK;
        //} else {
            //this.turnColor = TeamColor.WHITE;
        //}

        this.turnColor = team;
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
        ChessPiece observedPiece = this.board.getPiece(startPosition);
        Collection<ChessMove> legalMoves = observedPiece.pieceMoves(this.board, startPosition);
        Collection<ChessMove> newLegalMoves = new ArrayList<>();

        if (observedPiece.getPieceType() == null) {return null;}
        //else if (observedPiece.getPieceType() == ChessPiece.PieceType.KING) {
            //for (ChessMove move : legalMoves) {
                //if move.getEndPosition(){//return newLegalMoves; return legalMoves;}
        for (ChessMove move : legalMoves) {
            ChessBoard boardCopy = copyBoard(this.board);
            boardCopy.addPiece(move.getStartPosition(), null);
            boardCopy.addPiece(move.getEndPosition(), observedPiece);
            ChessGame upDatedGame = new ChessGame(this.turnColor, boardCopy);

            if (!upDatedGame.isInCheck(observedPiece.getTeamColor())){
                newLegalMoves.add(move);
            }
        }
        return newLegalMoves;
    }

    //for safe cloning
    //@Override
    //protected Object clone() throws CloneNotSupportedException {
        //return super.clone();
    //}

    public ChessBoard copyBoard(ChessBoard board) {
        ChessBoard boardCopy = new ChessBoard();
        boardCopy = board;
        return boardCopy;
    }

    /**
         * Makes a move in a chess game
         *
         * @param move chess move to preform
         * @throws InvalidMoveException if move is invalid
         */
        public void makeMove (ChessMove move) throws InvalidMoveException {
            throw new RuntimeException("Not implemented");
        }

        /**
         * Determines if the given team is in check
         *
         * @param teamColor which team to check for check
         * @return True if the specified team is in check
         */
        public boolean isInCheck (TeamColor teamColor){
             ChessBoard observedBoard = this.board;
             ChessPosition kingsThrone = findKing(teamColor);
             for (int row = 0; row < 8; row++) {
                 for (int col = 0; col < 8; col++) {
                     ChessPosition observedPosition = new ChessPosition(row+1, col+1);
                     ChessPiece observedPiece = observedBoard.getPiece(observedPosition);
                     if (observedPiece.getTeamColor() != teamColor) {
                         Collection<ChessMove> enemyMoves = observedPiece.pieceMoves(this.board, observedPosition);
                         for (ChessMove move : enemyMoves){
                             if (move.getEndPosition() == kingsThrone) {
                                 return true;
                             }
                         }
                     }
                 }
             }
             return false;
        }

        /**
         * finds the position of the king of the given teamColor
         *
         * @return
         */

        public ChessPosition findKing (TeamColor teamColor){
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    ChessPosition observedPosition = new ChessPosition(row+1, col+1);
                    ChessPiece observedPiece = this.board.getPiece(observedPosition);
                    if (observedPiece.getPieceType() == ChessPiece.PieceType.KING && observedPiece.getTeamColor() == teamColor) {
                        return observedPosition;
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
        public boolean isInCheckmate (TeamColor teamColor) {
            Collection<ChessMove> legalMoves;

            if(isInCheck(teamColor)){
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        ChessPosition observedPosition = new ChessPosition(row+1, col+1);
                        ChessPiece chessPiece = this.board.getPiece(observedPosition);
                        try{
                            legalMoves = validMoves(observedPosition);
                            if (!legalMoves.isEmpty()) {
                                return false;
                            }
                        }catch (RuntimeException e){
                            System.err.println("Illegal move.");
                        }
                    }
                }
                return true;
            }else{
                return false;
            }
        }

        /**
         * Determines if the given team is in stalemate, which here is defined as having
         * no valid moves
         *
         * @param teamColor which team to check for stalemate
         * @return True if the specified team is in stalemate, otherwise false
         */
        public boolean isInStalemate (TeamColor teamColor){
            throw new RuntimeException("Not implemented");
        }

        /**
         * Sets this game's chessboard with a given board
         *
         * @param board the new board to use
         */
        public void setBoard(ChessBoard board){
            this.board = board;
        }

        /**
         * Gets the current chessboard
         *
         * @return the chessboard
         */
        public ChessBoard getBoard () {
            return this.board;
        }

    @Override
    public String toString() {
        return "ChessGame{" +
                "turnColor=" + turnColor +
                ", board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return turnColor == chessGame.turnColor && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turnColor, board);
    }
}
