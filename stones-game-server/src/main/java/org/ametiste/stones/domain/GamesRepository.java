package org.ametiste.stones.domain;

import org.ametiste.stones.domain.model.Game;
import org.ametiste.stones.domain.model.GameStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by atlantis on 10/3/15.
 */
public interface GamesRepository {

//    boolean containsGame(UUID gameId);

    Game loadGame(UUID gameId);

    Optional<Game> findGameWithStatus(GameStatus status);

    void saveGame(Game game);

    List<Game> findGamesOlder(List<GameStatus> gameStatuses, long borderTime);

    void deleteGame(Game game);
}
