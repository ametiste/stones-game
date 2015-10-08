package org.ametiste.stones.interfaces.web.dto;

import java.util.UUID;

/**
 * Created by atlantis on 10/4/15.
 */
public class GameIdDto {

    private UUID gameId;

    public GameIdDto(UUID gameId) {
        this.gameId = gameId;
    }

    public UUID getGameId() {
        return gameId;
    }
}
