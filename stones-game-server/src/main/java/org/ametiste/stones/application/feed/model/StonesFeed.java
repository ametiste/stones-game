package org.ametiste.stones.application.feed.model;

import org.ametiste.stones.domain.model.GameState;
import org.ametiste.stones.domain.model.GameStatus;


import java.util.UUID;

/**
 * Created by atlantis on 10/3/15.
 */
public class StonesFeed {


    private final UUID gameId;
    private final GameStatus status;
    private final UUID roundOwner;
    private final Object firstUserBoard;
    private final Object secondUserBoard;
    private final UUID winner;


    public StonesFeed(UUID gameId, GameState state) {

        this.gameId = gameId;
        this.status = state.getStatus();
        this.roundOwner = state.getRoundOwner();
        this.winner = state.getWinnerId();
        this.firstUserBoard = state.getFirstBoard();
        this.secondUserBoard = state.getSecondBoard();
    }

    public UUID getGameId() {
        return gameId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Object getFirstUserBoard() {
        return firstUserBoard;
    }

    public Object getSecondUserBoard() {
        return secondUserBoard;
    }

    public UUID getRoundOwner() {
        return roundOwner;
    }

    public UUID getWinner() {
        return winner;
    }
}
