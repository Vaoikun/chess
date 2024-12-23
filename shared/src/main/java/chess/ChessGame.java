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
    public boolean isResigned = false;
    public ChessGame.TeamColor turnColor;
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
        //inserted here to avoid null error
        if (observedPiece == null) {return null;}
        Collection<ChessMove> legalMoves = observedPiece.pieceMoves(this.board, startPosition);
        Collection<ChessMove> newLegalMoves = new ArrayList<>();
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

    public ChessBoard copyBoard(ChessBoard board) {
        ChessBoard boardCopy = new ChessBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row+1, col+1);
                ChessPiece piece = board.getPiece(position);
                boardCopy.addPiece(position, piece);
            }
        }
        return boardCopy;
    }

    /**
         * Makes a move in a chess game
         *
         * @param move chess move to preform
         * @throws InvalidMoveException if move is invalid
         */
        public void makeMove (ChessMove move) throws InvalidMoveException {
            Collection<ChessMove> legalMoves = this.validMoves(move.getStartPosition());
            ChessPiece observedPiece = this.board.getPiece(move.getStartPosition());
            // legalMoves can be null. (not empty)
            if (legalMoves == null) {
                throw new InvalidMoveException("Legal moves are null.");
            }

            if (legalMoves.contains(move)) {
                if (observedPiece.getTeamColor() != this.turnColor) {
                    throw new InvalidMoveException("It's not turn yet.");
                }else {
                    this.board.addPiece(move.getStartPosition(), null);
                    if (move.getPromotionPiece() == null) {
                        this.board.addPiece(move.getEndPosition(), observedPiece);
                    } else { //promotion step
                        ChessPiece promotionPiece = new ChessPiece(observedPiece.getTeamColor(), move.getPromotionPiece());
                        this.board.addPiece(move.getEndPosition(), promotionPiece);
                    }
                    changeTurn();
                }
            }else{
                throw new InvalidMoveException("Illegal moves.");
            }
        }

    /**
     * changes turn
     */
    public  void changeTurn(){
            if (this.turnColor == TeamColor.WHITE) {
                this.turnColor = TeamColor.BLACK;
            }else{
                this.turnColor = TeamColor.WHITE;
            }
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
                     //to avoid null error
                     if(observedPiece == null){
                         continue;
                     }
                     if (observedPiece.getTeamColor() != teamColor) {
                         Collection<ChessMove> enemyMoves = observedPiece.pieceMoves(observedBoard, observedPosition);
                         if (checkMoveIsKingsThrone(enemyMoves, kingsThrone)){
                             return true;
                         }
                     }
                 }
             }
             return false;
        }

        private boolean checkMoveIsKingsThrone(Collection<ChessMove> enemyMoves, ChessPosition kingsThrone){
            for (ChessMove move : enemyMoves){
                if (move.getEndPosition().equals(kingsThrone)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * finds the position of the king of the; given teamColor
         *
         * @return
         */

        public ChessPosition findKing (TeamColor teamColor){
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    ChessPosition observedPosition = new ChessPosition(row+1, col+1);
                    ChessPiece observedPiece = this.board.getPiece(observedPosition);
                    //if so skip next one to avoid error
                    if (observedPiece == null) {
                        continue;
                    }
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

            if(!isInCheck(teamColor)){return false;}
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    ChessPosition observedPosition = new ChessPosition(row+1, col+1);
                    ChessPiece observedPiece = this.board.getPiece(observedPosition);
                    legalMoves = validMoves(observedPosition);
                    if (observedPiece != null && observedPiece.getTeamColor() == teamColor && !legalMoves.isEmpty()) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * Determines if the given team is in stalemate, which here is defined as having
         * no valid moves
         *
         * @param teamColor which team to check for stalemate
         * @return True if the specified team is in stalemate, otherwise false
         */
        public boolean isInStalemate (TeamColor teamColor){
            Collection<ChessMove> legalMoves;
            boolean isInStalemate = true;

            if(isInCheckmate(teamColor)){return false;}
            ChessGame.TeamColor originalTeamColor = teamColor;
            if(this.turnColor != teamColor){
                changeTurn();
            }
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    ChessPosition observedPosition = new ChessPosition(row+1, col+1);
                    ChessPiece observedPiece = this.board.getPiece(observedPosition);
                    legalMoves = validMoves(observedPosition);
                    if (observedPiece != null && observedPiece.getTeamColor() == teamColor && !legalMoves.isEmpty()) {
                        isInStalemate = false;
                        changeTurn();
                    }
                }
            }
            if(this.turnColor != originalTeamColor){
                changeTurn();
            }
            return isInStalemate;
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
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessGame chessGame = (ChessGame) o;
        return turnColor == chessGame.turnColor && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turnColor, board);
    }
}
