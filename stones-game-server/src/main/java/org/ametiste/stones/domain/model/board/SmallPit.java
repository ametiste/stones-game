package org.ametiste.stones.domain.model.board;

/**
 * Represents small pit of the board
 * Created by atlantis on 10/3/15.
 */
public class SmallPit {

    private int stones;

    public SmallPit() {
        this.stones = 6;
    }


    public int getAll() {
        int stones = this.stones;
        this.stones = 0;
        return stones;
    }

    public boolean isEmpty() {
        return stones==0;
    }

    public void addStone() {
        stones++;
    }

    public int stoneNumber() {
        return stones;
    }

}
