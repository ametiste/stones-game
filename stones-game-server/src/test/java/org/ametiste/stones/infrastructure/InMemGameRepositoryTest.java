package org.ametiste.stones.infrastructure;

import org.ametiste.stones.domain.GameNotFoundException;
import org.ametiste.stones.domain.model.Game;
import org.ametiste.stones.domain.model.GameState;
import org.ametiste.stones.domain.model.GameStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by atlantis on 10/3/15.
 */
public class InMemGameRepositoryTest {

    private InMemGameRepository repo;

    private UUID gameId1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private UUID gameId2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private UUID gameId3 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    @Mock
    private Game game1;

    @Mock
    private Game game2;

    @Mock
    private Game game3;

    @Mock
    private GameState gameState;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        repo = new InMemGameRepository(2);
        when(game1.getId()).thenReturn(gameId1);
        when(game2.getId()).thenReturn(gameId2);
        when(game3.getId()).thenReturn(gameId3);
    }

    @Test
    public void testLoadGame() throws Exception {
        repo.saveGame(game1);
        assertEquals(game1, repo.loadGame(gameId1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveGameExceedCapacity() {
        repo.saveGame(game1);
        repo.saveGame(game2);
        repo.saveGame(game3);
    }

    @Test
    public void saveGameExceedUpdateAllowed() {
        repo.saveGame(game1);
        repo.saveGame(game2);
        repo.saveGame(game1);
    }

    @Test
    public void testFindGameWithStatus() throws Exception {
        when(game1.inStatus(GameStatus.STARTED)).thenReturn(false);
        when(game2.inStatus(GameStatus.STARTED)).thenReturn(true);
        repo.saveGame(game1);
        repo.saveGame(game2);
        Optional<Game> gameWithStatus = repo.findGameWithStatus(GameStatus.STARTED);
        assertTrue(gameWithStatus.isPresent());
        assertEquals(game2, gameWithStatus.get());

    }

    @Test
    public void testFindGameWithStatusNotExists() throws Exception {
        when(game1.inStatus(GameStatus.STARTED)).thenReturn(false);
        when(game2.inStatus(GameStatus.STARTED)).thenReturn(false);
        repo.saveGame(game1);
        repo.saveGame(game2);
        Optional<Game> gameWithStatus = repo.findGameWithStatus(GameStatus.STARTED);
        assertFalse(gameWithStatus.isPresent());

    }

    @Test
    public void testFindGamesOlder() throws Exception {
        long time = System.currentTimeMillis() - 1000;

        when(game1.getState()).thenReturn(gameState);
        when(game2.getState()).thenReturn(gameState);
        when(gameState.getStatus()).thenReturn(GameStatus.CANCELLED);
        when(game1.updatedSince(time)).thenReturn(true);
        when(game2.updatedSince(time)).thenReturn(false);
        repo.saveGame(game1);
        repo.saveGame(game2);
        List<Game> gamesOlder = repo.findGamesOlder(Arrays.asList(GameStatus.CANCELLED), time);
        assertTrue(gamesOlder.contains(game2));
        assertEquals(1, gamesOlder.size());
    }

    @Test(expected = GameNotFoundException.class)
    public void testDeleteGame() throws Exception {
        repo.saveGame(game1);
        repo.deleteGame(game1);
        repo.loadGame(gameId1);


    }
}