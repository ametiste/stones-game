package org.ametiste.stones.application.feed;

import org.ametiste.stones.application.feed.model.StonesFeed;
import org.ametiste.stones.domain.GamesRepository;
import org.ametiste.stones.domain.model.Game;
import org.ametiste.stones.domain.model.GameState;
import org.ametiste.stones.domain.model.GameStatus;
import org.ametiste.stones.domain.model.board.BoardState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by atlantis on 10/3/15.
 */
public class PullGameFeedTest {

    private PullGameFeed gameFeed;
    @Mock
    private GamesRepository repository;

    @Mock
    private Game game;
    private UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private UUID roundOwner = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    @Mock
    private BoardState userState;
    @Mock
    private BoardState opponentState;

    private GameState state;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        gameFeed = new PullGameFeed(repository);
        state = new GameState(GameStatus.CANCELLED, null, roundOwner, userState, opponentState);
    }

    @Test
    public void testGetSnapshot() throws Exception {
        when(repository.loadGame(id)).thenReturn(game);
        when(game.getState()).thenReturn(state);
        StonesFeed snapshot = gameFeed.getSnapshot(id);
        assertEquals(GameStatus.CANCELLED, snapshot.getStatus());
        assertEquals(id, snapshot.getGameId());
        assertEquals(roundOwner, snapshot.getRoundOwner());
        assertEquals(userState, snapshot.getFirstUserBoard());
        assertEquals(opponentState, snapshot.getSecondUserBoard());
        assertNull(snapshot.getWinner());
    }


}