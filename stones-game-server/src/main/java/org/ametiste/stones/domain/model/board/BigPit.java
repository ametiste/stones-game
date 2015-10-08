package org.ametiste.stones.domain.model.board;

/**
 * Represents big pit of a board
 * Initial stones number is 0 (its empty at start of game)
 * Created by atlantis on 10/3/15.
 */
public class BigPit  {

    private int stones;

    public BigPit() {
        stones = 0;
    }

    public void add(int captured) {
        this.stones = stones + captured;
    }

    public int stoneNumber() {
        return stones;
    }
}
