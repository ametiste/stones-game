package org.ametiste.stones.application;

import org.ametiste.stones.domain.model.GameMove;

import java.util.UUID;

/**
 * Created by atlantis on 10/3/15.
 */
public interface StonesServer {

    void move(UUID gameId, GameMove gameMove);

    UUID join(UUID userId);

    void cancel(UUID gameId, UUID userId);
}
