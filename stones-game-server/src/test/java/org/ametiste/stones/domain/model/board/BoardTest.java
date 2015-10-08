package org.ametiste.stones.domain.model.board;

import org.ametiste.stones.domain.EmptyPitException;
import org.ametiste.stones.domain.OutOfPitBoundsException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by atlantis on 10/3/15.
 */
public class BoardTest {

    @Mock
    private Board opponentBoard;

    private Board board;
    private UUID user = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        board = new Board(user);
        board.addOpponentBoard(opponentBoard);
    }

    @Test
    public void testGetUser() throws Exception {
        assertEquals(user, board.getUser());
    }

    @Test(expected = OutOfPitBoundsException.class)
    public void testMoveOutOfBounds() throws Exception {
        board.move(-1);
    }

    @Test(expected = OutOfPitBoundsException.class)
    public void testMoveOutOfBoundsMore() throws Exception {
        board.move(6);
    }

    @Test(expected = EmptyPitException.class)
    public void testMoveZeroPit() throws Exception {
        board.move(0);
        board.move(0);
    }

    @Test
    public void testMove() throws Exception {
        board.move(4);
        assertTrue(board.getState().getPits().get(5) == 7);
        verify(opponentBoard, times(1)).left(4);
    }

    @Test
    public void testMoveCapture() throws Exception {

        when(opponentBoard.capture(2)).thenReturn(3);
        board.move(0);
        board.move(1);
        board.move(2);
        board.move(3);
        board.move(4);
        board.move(5);
        //make it zero;
        board.left(3); //one in 0 1 2 pits
        board.move(2); //move from pit 2 to empty pit 3
        verify(opponentBoard).capture(2);
        assertEquals(10,board.getState().getBigPit());
    }

    @Test
    public void testMoveNoMovePassed() throws Exception {

        Board b = board.move(0);
        assertEquals(board, b);
    }

    @Test
    public void testMovePassed() throws Exception {

        Board b = board.move(1);
        assertEquals(opponentBoard, b);
    }

    @Test
    public void testOutOfMoves() throws Exception {
        board.move(0);
        board.move(1);
        board.move(2);
        board.move(3);
        board.move(4);
        board.move(5);
        assertTrue(board.outOfMoves());

    }

    @Test
    public void testLeftPassToOpponent() {
        board.left(8);
        verify(opponentBoard).left(2);
        assertEquals(0, board.getState().getBigPit());

    }

    @Test
    public void testCapture() {
        assertEquals(6,board.capture(0));
        assertTrue(board.getState().getPits().get(0)==0);

    }


    @Test
    public void testGetState() throws Exception {
        assertEquals(user, board.getState().getUserId());
        assertEquals(0, board.getState().getBigPit());
        assertArrayEquals(new Integer[]{6, 6, 6, 6, 6, 6}, board.getState().getPits().toArray());

    }
}