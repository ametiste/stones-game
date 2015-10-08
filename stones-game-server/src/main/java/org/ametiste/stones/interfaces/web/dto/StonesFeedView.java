package org.ametiste.stones.interfaces.web.dto;

import org.ametiste.stones.domain.model.GameStatus;
import org.ametiste.stones.application.feed.model.StonesFeed;

import java.util.UUID;

/**
 * Created by atlantis on 10/3/15.
 */
public class StonesFeedView {

    private final UUID gameId;
    private final GameStatus status;
    private final UUID roundOwner;
    private final Object firstUserBoard;
    private final Object secondUserBoard;
    private final UUID winner;

    public StonesFeedView(StonesFeed snapshot) {
        this.gameId = snapshot.getGameId();
        this.status = snapshot.getStatus();
        this.winner = snapshot.getWinner();
        this.roundOwner = snapshot.getRoundOwner();
        this.firstUserBoard = snapshot.getFirstUserBoard();
        this.secondUserBoard = snapshot.getSecondUserBoard();

    }

    public UUID getGameId() {
        return gameId;
    }

    public GameStatus getStatus() {
        return status;
    }


    public UUID getRoundOwner() {
        return roundOwner;
    }

    public Object getFirstUserBoard() {
        return firstUserBoard;
    }

    public Object getSecondUserBoard() {
        return secondUserBoard;
    }

    public UUID getWinner() {
        return winner;
    }
}
