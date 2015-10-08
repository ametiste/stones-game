package org.ametiste.stones.domain.model.board;

import java.util.Arrays;

/**
 * For preparing games that dont have second user yet but still should return some state to client
 * Created by atlantis on 10/4/15.
 */
public class StubBoard extends Board {

    public StubBoard() {
        super(null);
    }

    public BoardState getState() {

        SmallPit smallPit = new SmallPit();
        smallPit.getAll();
        return new BoardState(null,Arrays.asList(smallPit, smallPit,smallPit,
                smallPit,smallPit,smallPit), new BigPit());
    }
}
