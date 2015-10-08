package org.ametiste.stones.domain.model;

import org.ametiste.stones.domain.IncorrectGameStatusException;
import org.ametiste.stones.domain.UserAlreadyJoinedException;
import org.ametiste.stones.domain.WrongUserTurnException;
import org.ametiste.stones.domain.model.board.Board;
import org.ametiste.stones.domain.model.board.StubBoard;

import java.util.UUID;

/**
 * Common logic of game, that includes both opponent and own boards
 * Created by atlantis on 10/3/15.
 */
public class Game {

    private long statusUpdateTime;
    private Board roundOwner;
    private UUID id;
    private Board firstBoard;
    private Board secondBoard;
    private GameStatus status;
    private UUID winner = null;

    /**
     * Game is created when first user joins, no second board exists yet, and game status is PREPARING
     * Triggers update time(creates last updated time for cleanup needs)
     */
    public Game(UUID id, UUID userId) {
        updateTime();
        this.id = id;
        this.firstBoard = new Board(userId);
        this.secondBoard = new StubBoard();
        this.status = GameStatus.PREPARING;
        this.roundOwner = firstBoard;
    }

    /**
     * Move of a user. Can be done only when game is STARTED and the round is user's
     */
    public void move(GameMove gameMove) {
        if(!status.equals(GameStatus.STARTED)) {
            throw new IncorrectGameStatusException();
        }

        if(!gameMove.getUserId().equals(roundOwner.getUser())) {
            throw new WrongUserTurnException();
        }

        updateTime();
        roundOwner = roundOwner.move(gameMove.getPitNumber());
        if(firstBoard.outOfMoves() || secondBoard.outOfMoves()) {
            winner = firstBoard.finish();
            this.status = GameStatus.FINISHED;
        }

    }

    /**
     * Adds second user to existing game. User cant be added to own game, and cant be added to game with any state but
     * PREPARING
     * Triggers update of game activity time
     */
    public void addPlayer(UUID userId) {
        if(firstBoard.getUser().equals(userId)) {
            throw new UserAlreadyJoinedException();
        }
        if(!status.equals(GameStatus.PREPARING)) {
            throw new IncorrectGameStatusException();
        }
        secondBoard = new Board(userId);
        this.startGame();

    }

    private void startGame() {
        updateTime();
        firstBoard.addOpponentBoard(secondBoard);
        this.status = GameStatus.STARTED;
    }

    public boolean inStatus(GameStatus state) {
        return state.equals(this.status);
    }

    public UUID getId() {
        return id;
    }

    public GameState getState() {
        return new GameState(this.status, this.winner, roundOwner.getUser(),
                firstBoard.getState(), secondBoard.getState());
    }

    /**
     * Moves game to cancelled state, userId of user initiated cancel exists for future improvements
     * Triggers update of game activity time
     */
    public void cancel(UUID userId) {
        updateTime();
        this.status = GameStatus.CANCELLED;
        //todo maybe later will define cancelled by who and who won
    }

    /**
     * Moves game to timeout state
     * Triggers update of game activity time
     */
    public void timeout() {
        updateTime();
        this.status = GameStatus.TIMEDOUT;
    }

    private void updateTime() {
        statusUpdateTime = System.currentTimeMillis();
    }

    public boolean updatedSince(long time) {

        return statusUpdateTime > time;
    }
}
