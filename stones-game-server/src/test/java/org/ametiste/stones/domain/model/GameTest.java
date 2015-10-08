package org.ametiste.stones.domain.model;

import org.ametiste.stones.domain.IncorrectGameStatusException;
import org.ametiste.stones.domain.UserAlreadyJoinedException;
import org.ametiste.stones.domain.WrongUserTurnException;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by atlantis on 10/3/15.
 */
public class GameTest {

    private Game game;
    private UUID gameId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private UUID user = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private UUID opponent = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    @Before
    public void setUp() throws Exception {
        game = new Game(gameId, user);
    }

    @Test
    public void testInitial() throws Exception {
        assertEquals(user, game.getState().getFirstBoard().getUserId());
        assertEquals(gameId, game.getId());
        assertEquals(GameStatus.PREPARING, game.getState().getStatus());

        assertEquals(user, game.getState().getFirstBoard().getUserId());
        assertEquals(null, game.getState().getSecondBoard().getUserId());

        assertEquals(0, game.getState().getFirstBoard().getBigPit());
        assertEquals(0, game.getState().getSecondBoard().getBigPit());
        assertArrayEquals(new Integer[]{6, 6, 6, 6, 6, 6}, game.getState().getFirstBoard().getPits().toArray());
        assertArrayEquals(new Integer[]{0, 0, 0, 0, 0, 0}, game.getState().getSecondBoard().getPits().toArray());
    }

    @Test(expected = IncorrectGameStatusException.class)
    public void testMoveBeforeStart() throws Exception {
        game.move(new GameMove(opponent, 0));
    }

    @Test(expected = WrongUserTurnException.class)
    public void testMoveAfterWrongTurn() throws Exception {
        game.addPlayer(opponent);
        game.move(new GameMove(opponent, 0));
    }

    @Test
    public void testAddPlayer() throws Exception {
        game.addPlayer(opponent);
        assertEquals(GameStatus.STARTED, game.getState().getStatus());
        assertEquals(opponent, game.getState().getSecondBoard().getUserId());
    }

    @Test(expected = UserAlreadyJoinedException.class)
    public void testAddSamePlayer() throws Exception {
        game.addPlayer(user);
    }


    @Test(expected = IncorrectGameStatusException.class)
    public void testAddPlayerToTimeoutGame() throws Exception {
        game.timeout();
        game.addPlayer(opponent);
    }

    @Test
    public void testMoveNormal() throws Exception {
        game.addPlayer(opponent);
        game.move(new GameMove(user, 1));
        assertEquals(GameStatus.STARTED, game.getState().getStatus());
        assertEquals(opponent, game.getState().getRoundOwner());
    }

    @Test
    public void testInStatus() throws Exception {
        assertTrue(game.inStatus(GameStatus.PREPARING));
        game.timeout();
        assertTrue(game.inStatus(GameStatus.TIMEDOUT));
    }


    @Test
    public void testGetState() throws Exception {
        game.timeout();
        GameState state = game.getState();
        assertEquals(GameStatus.TIMEDOUT, state.getStatus());
        assertEquals(0, state.getFirstBoard().getBigPit());
        assertEquals(0, state.getSecondBoard().getBigPit());

    }

    @Test
    public void testCancel() throws Exception {
        game.cancel(user);
        assertEquals(GameStatus.CANCELLED, game.getState().getStatus());
    }

    @Test
    public void testTimeout() throws Exception {
        game.timeout();
        assertEquals(GameStatus.TIMEDOUT, game.getState().getStatus());
    }

    @Test
    public void testUpdated() throws Exception {
        game.timeout();
        assertTrue(game.updatedSince(System.currentTimeMillis() - 1000));
    }

    @Test
    public void testNotUpdated() throws Exception {
        game.timeout();
        Thread.sleep(200);
        assertFalse(game.updatedSince(System.currentTimeMillis()-50));
    }
}