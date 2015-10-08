package org.ametiste.stones.application;

import org.ametiste.stones.domain.*;
import org.ametiste.stones.domain.model.Game;
import org.ametiste.stones.domain.model.GameMove;
import org.ametiste.stones.domain.model.GameStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * Simple game server that provides high level logic of game
 * Created by atlantis on 10/3/15.
 */
public class SimpleStonesServer implements StonesServer {

    private GamesRepository repository;

    public SimpleStonesServer(GamesRepository repository) {
        this.repository = repository;
    }

    /**
     * If game exists, move is applied to game.
     */
    @Override
    public void move(UUID gameId, GameMove gameMove) {
        Game game = repository.loadGame(gameId);
        game.move(gameMove);
        repository.saveGame(game);

    }

    /**
     * Joins user to existing game that is being prepared or creates new game for that user
     */
    @Override
    public UUID join(UUID userId) {
        Game game;
        Optional<Game> preparedGame = repository.findGameWithStatus(GameStatus.PREPARING);
        if(preparedGame.isPresent()) {
            game = preparedGame.get();
            game.addPlayer(userId);
        }
        else {
            game = new Game(UUID.randomUUID(), userId);
        }

        repository.saveGame(game);

        return game.getId();
    }

    /**
     * Moves game to CANCELLED state
     */
    @Override
    public void cancel(UUID gameId, UUID userId) {
        Game game = repository.loadGame(gameId);
        game.cancel(userId);
        repository.saveGame(game); //obsolete with inmem repository

    }
}
