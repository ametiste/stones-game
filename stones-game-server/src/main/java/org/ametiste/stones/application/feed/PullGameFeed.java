package org.ametiste.stones.application.feed;

import org.ametiste.stones.domain.GamesRepository;
import org.ametiste.stones.application.feed.model.StonesFeed;

import java.util.UUID;

/**
 * Implementation that provides feed only by request
 * Created by atlantis on 10/3/15.
 */
public class PullGameFeed implements GameFeed {


    private GamesRepository repository;

    public PullGameFeed(GamesRepository repository) {
        this.repository = repository;
    }

    @Override
    public StonesFeed getSnapshot(UUID gameId) {
        return new StonesFeed(gameId, repository.loadGame(gameId).getState());
    }
}
