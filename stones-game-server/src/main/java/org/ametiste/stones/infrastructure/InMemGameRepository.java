package org.ametiste.stones.infrastructure;

import org.ametiste.stones.domain.GameNotFoundException;
import org.ametiste.stones.domain.GamesRepository;
import org.ametiste.stones.domain.model.Game;
import org.ametiste.stones.domain.model.GameStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Prototype implementation of games repository, based on inmemory hashmap.
 * Created by atlantis on 10/3/15.
 */
public class InMemGameRepository implements GamesRepository {

    //NOTE this implementation is prototype only and cant be used in multithread environment
    private Map<UUID, Game> games;
    private int maxGamesNumber;

    public InMemGameRepository(int maxGamesNumber) {
        this.maxGamesNumber = maxGamesNumber;
        games = new HashMap<>();
    }

    @Override
    public Game loadGame(UUID gameId) {
        if(!games.containsKey(gameId)) {
            throw new GameNotFoundException();
        }
        return games.get(gameId);
    }

    @Override
    public Optional<Game> findGameWithStatus(GameStatus status) {
        return games.values().stream().filter((game) -> game.inStatus(status)).findAny();
    }

    @Override
    public void saveGame(Game game) {
        if(games.size()>=maxGamesNumber && !games.containsKey(game.getId())) {
            throw new IllegalArgumentException("Maximum allowed games number achieved, cannot store more");
        }
        games.put(game.getId(), game);
    }

    @Override
    public List<Game> findGamesOlder(List<GameStatus> gameStatuses, long borderTime) {
        return games.values().stream().filter((game) -> gameStatuses.contains(game.getState().getStatus()) &&
                !game.updatedSince(borderTime)).collect(Collectors.toList());
    }

    @Override
    public void deleteGame(Game game) {
        games.remove(game.getId());
    }

}
