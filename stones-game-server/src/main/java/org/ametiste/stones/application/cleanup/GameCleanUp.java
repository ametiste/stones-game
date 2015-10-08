package org.ametiste.stones.application.cleanup;

import org.ametiste.stones.domain.GamesRepository;
import org.ametiste.stones.domain.model.Game;
import org.ametiste.stones.domain.model.GameStatus;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;


/**
 * Cleans up games that werent active for some configurable time
 * Created by atlantis on 10/6/15.
 */
public class GameCleanUp {

    private static final List<GameStatus> ACTIVE_GAME_STATUSES =
            Arrays.asList(GameStatus.PREPARING, GameStatus.STARTED);
    private static final List<GameStatus> INACTIVE_GAME_STATUSES =
            Arrays.asList(GameStatus.CANCELLED, GameStatus.FINISHED, GameStatus.TIMEDOUT);
    private GamesRepository repository;
    private final int unfinishedGamesTimeoutInMs;
    private final int finishedGamesTimeoutInMs;

    public GameCleanUp(GamesRepository repository, int unfinishedGamesTimeoutInMs, int finishedGamesTimeoutInMs) {
        this.repository = repository;
        this.unfinishedGamesTimeoutInMs = unfinishedGamesTimeoutInMs;
        this.finishedGamesTimeoutInMs = finishedGamesTimeoutInMs;
    }

    @Scheduled(fixedRate = 60000) // 60*1000 - every 1 minute
    /**
     * Every minute moves active games that had no changes for configured time, to TIMEDOUT state, and removes
     * inactive games that exist longer than configured time.
     */
    public void cleanup() {

        long currentTime = System.currentTimeMillis();

        inactivateActive(currentTime);
        deleteInactive(currentTime);

    }

    private void deleteInactive(long currentTime) {
        List<Game> finishedGames = repository.findGamesOlder(INACTIVE_GAME_STATUSES, currentTime-finishedGamesTimeoutInMs);
        finishedGames.forEach(repository::deleteGame);
    }

    private void inactivateActive(long currentTime) {
        List<Game> games = repository.findGamesOlder(ACTIVE_GAME_STATUSES, currentTime-unfinishedGamesTimeoutInMs);
        games.forEach(Game::timeout);
        games.forEach(repository::saveGame);
    }

}
