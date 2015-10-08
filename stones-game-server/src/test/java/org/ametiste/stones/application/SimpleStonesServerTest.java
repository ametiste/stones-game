package org.ametiste.stones.application;

import org.ametiste.stones.domain.GamesRepository;
import org.ametiste.stones.domain.model.Game;
import org.ametiste.stones.domain.model.GameMove;
import org.ametiste.stones.domain.model.GameStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by atlantis on 10/3/15.
 */
public class SimpleStonesServerTest {

    private SimpleStonesServer server;
    @Mock
    private GamesRepository repository;

    @Mock
    private Game game;
    private UUID id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    @Mock
    private GameMove move;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        server = new SimpleStonesServer(repository);
    }

    @Test
    public void testMove() throws Exception {

        when(repository.loadGame(id)).thenReturn(game);
        server.move(id, move);

        verify(repository, times(1)).loadGame(id);
        verify(game, times(1)).move(move);
        verify(repository, times(1)).saveGame(game);

    }

    @Test
    public void testJoinFirst() throws Exception {

        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
        Optional<Game> nothing = Optional.empty();
        when(repository.findGameWithStatus(GameStatus.PREPARING)).thenReturn(nothing);
        UUID gameId = server.join(userId);
        verify(repository, times(1)).findGameWithStatus(GameStatus.PREPARING);
        verify(repository, times(1)).saveGame(captor.capture());
        assertEquals(gameId, captor.getValue().getId());

    }

    @Test
    public void testJoinSecond() throws Exception {
        Optional<Game> something = Optional.of(game);
        when(repository.findGameWithStatus(GameStatus.PREPARING)).thenReturn(something);
        when(game.getId()).thenReturn(id);
        UUID gameId = server.join(userId);
        verify(repository, times(1)).findGameWithStatus(GameStatus.PREPARING);
        verify(game, times(1)).addPlayer(userId);
        verify(repository, times(1)).saveGame(game);
        assertEquals(id, gameId);
    }

    @Test
    public void testCancel() throws Exception {
        when(repository.loadGame(id)).thenReturn(game);
        server.cancel(id, userId);
        verify(repository, times(1)).loadGame(id);
        verify(game, times(1)).cancel(userId);
    }
}