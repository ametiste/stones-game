package org.ametiste.stones.domain.model;

import org.ametiste.stones.domain.model.board.BoardState;

import java.util.UUID;

/**
 * Created by atlantis on 10/3/15.
 */
public class GameState {

    private GameStatus status;
    private final BoardState firstBoard;
    private final BoardState secondBoard;
    private final UUID winnerId;
    private final UUID roundOwner;

    public GameState(GameStatus status, UUID winnerId, UUID roundOwner, BoardState firstBoard, BoardState secondBoard) {
        this.status = status;
        this.winnerId = winnerId;
        this.roundOwner = roundOwner;
        this.firstBoard = firstBoard;
        this.secondBoard = secondBoard;
    }

    public GameStatus getStatus() {
        return status;
    }

    public BoardState getFirstBoard() {
        return firstBoard;
    }

    public BoardState getSecondBoard() {
        return secondBoard;
    }

    public UUID getRoundOwner() {
        return roundOwner;
    }

    public UUID getWinnerId() {
        return winnerId;
    }
}
