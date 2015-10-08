package org.ametiste.stones.application.feed;

import org.ametiste.stones.application.feed.model.StonesFeed;

import java.util.UUID;

/**
 * Provides external source with state of game
 * Created by atlantis on 10/3/15.
 */
public interface GameFeed {
    /**
     * Provides external source with state of game with id gameId
     */
    StonesFeed getSnapshot(UUID gameId);
}
