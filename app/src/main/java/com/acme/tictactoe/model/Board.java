package com.acme.tictactoe.model;

import android.os.Parcel;
import android.os.Parcelable;

import static com.acme.tictactoe.model.Player.O;
import static com.acme.tictactoe.model.Player.X;

public class Board implements Parcelable {

    private Cell[][] cells = new Cell[3][3];

    private Player winner;
    private GameState state;
    private Player currentTurn;

    private enum GameState { IN_PROGRESS, FINISHED }

    public Board() {
        restart();
    }

    private Board(Parcel in)
    {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j].setValue(Player.fromInt(in.readInt()));
            }
        }
        winner = Player.fromInt(in.readInt());
        state = (in.readInt() == 0 ? GameState.IN_PROGRESS : GameState.FINISHED);
        currentTurn = Player.fromInt(in.readInt());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                dest.writeInt(cells[i][j].getValue().id);
            }
        }
        dest.writeInt(winner.id);
        dest.writeInt(state.ordinal());
        dest.writeInt(currentTurn.id);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<Board> CREATOR = new Creator<Board>()
    {
        @Override
        public Board createFromParcel(Parcel in)
        {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size)
        {
            return new Board[size];
        }
    };

    /**
     *  Restart or start a new game, will clear the board and win status
     */
    public void restart() {
        clearCells();
        winner = null;
        currentTurn = Player.X;
        state = GameState.IN_PROGRESS;
    }

    /**
     * Mark the current row for the player who's current turn it is.
     * Will perform no-op if the arguments are out of range or if that position is already played.
     * Will also perform a no-op if the game is already over.
     *
     * @param row 0..2
     * @param col 0..2
     * @return the player that moved or null if we did not move anything.
     *
     */
    public Player mark( int row, int col ) {

        Player playerThatMoved = null;

        if(isValid(row, col)) {

            cells[row][col].setValue(currentTurn);
            playerThatMoved = currentTurn;

            if(isWinningMoveByPlayer(currentTurn, row, col)) {
                state = GameState.FINISHED;
                winner = currentTurn;

            } else {
                // flip the current turn and continue
                flipCurrentTurn();
            }
        }
        else { // keep old value if the move was on board
            if (isCellValueAlreadySet(row, col)) {
                return cells[row][col].getValue();
            }
        }
        return playerThatMoved;
    }

    public Player valueAtCell(int row, int col) {
        return cells[row][col].getValue();
    }

    public Player getWinner() {
        return winner;
    }

    private void clearCells() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    private boolean isValid(int row, int col ) {
        if( state == GameState.FINISHED ) {
            return false;
        } else if( isOutOfBounds(row) || isOutOfBounds(col) ) {
            return false;
        } else if( isCellValueAlreadySet(row, col) ) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isOutOfBounds(int idx) {
        return idx < 0 || idx > 2;
    }

    private boolean isCellValueAlreadySet(int row, int col) {
        return cells[row][col].getValue() != null;
    }


    /**
     * Algorithm adapted from http://www.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe.html
     * @param player
     * @param currentRow
     * @param currentCol
     * @return true if <code>player</code> who just played the move at the <code>currentRow</code>, <code>currentCol</code>
     *              has a tic tac toe.
     */
    private boolean isWinningMoveByPlayer(Player player, int currentRow, int currentCol) {

        return (cells[currentRow][0].getValue() == player         // 3-in-the-row
                && cells[currentRow][1].getValue() == player
                && cells[currentRow][2].getValue() == player
                || cells[0][currentCol].getValue() == player      // 3-in-the-column
                && cells[1][currentCol].getValue() == player
                && cells[2][currentCol].getValue() == player
                || currentRow == currentCol            // 3-in-the-diagonal
                && cells[0][0].getValue() == player
                && cells[1][1].getValue() == player
                && cells[2][2].getValue() == player
                || currentRow + currentCol == 2    // 3-in-the-opposite-diagonal
                && cells[0][2].getValue() == player
                && cells[1][1].getValue() == player
                && cells[2][0].getValue() == player);
    }

    private void flipCurrentTurn() {
        currentTurn = currentTurn == X ? O : X;
    }

}
