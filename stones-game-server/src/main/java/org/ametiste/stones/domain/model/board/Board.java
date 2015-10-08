package org.ametiste.stones.domain.model.board;

import org.ametiste.stones.domain.EmptyPitException;
import org.ametiste.stones.domain.OutOfPitBoundsException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Represents board of a game for one user (i.e. game contains 2 boards in this implementation
 * Created by atlantis on 10/3/15.
 */
public class Board {

    private Board opponentBoard;
    private final UUID userId;
    private List<SmallPit> pits;
    private BigPit bigPit;

    /**
     * Creates initial board for user with id userId
     * 6 small pits and one big pit
     */
    public Board(UUID userId) {
        this.userId = userId;
        pits = new ArrayList<>(6);
        IntStream.rangeClosed(0,5).forEach(i-> pits.add(new SmallPit()));
        bigPit = new BigPit();

    }

    /**
     * When second user is registered, boards are linked to each other
     */
    public void addOpponentBoard(Board board) {
        opponentBoard = board;
        opponentBoard.opponentBoard = this;
    }

    public UUID getUser() {
        return userId;
    }

    /**
     *
     * Moves stone from pit with number pitNumber. If last stone is put into own empty pit, capturing is happened.
     * Move is passed.
     * If last stone is put into own big pit, move isnt passed.
     * If after all own small pits and bit pit got a stone and there are still stones left, stones are moved to opponent
     * board, with other rules of move, here named 'left'
     */
    public Board move(int pitNumber) {
        if(pitNumber<0 || pitNumber>5) {
            throw new OutOfPitBoundsException();
        }

        if(pits.get(pitNumber).isEmpty()) {
            throw new EmptyPitException();
        }

        int stones = pits.get(pitNumber).getAll();

        for(int i = pitNumber+1;i<pits.size(); i++) {
            stones--;
            SmallPit pit = pits.get(i);

            if(stones==0 ) {
                if(pit.isEmpty() ) { // last stone in empty own pit
                    int captured = opponentBoard.capture(pits.size() - 1 - i) + 1;
                    bigPit.add(captured);
                }
                else {
                    pit.addStone(); //we dont add stone to empty pit
                }
                return opponentBoard;
            }
            pit.addStone();
        }
        bigPit.add(1);
        stones--;
        if(stones==0) {
            return this;
        }

        opponentBoard.left(stones);
        return opponentBoard; //pass the move
    }

    /**
     * Happens when there are stones left after filling own board, and are passed to opponent, without any
     * capture and move keeping
     */
    // package friendly for testing
    void left(int stones) {
        int stonesToPass=stones;
        for(int i = 0;i<pits.size() && stonesToPass>0 ; i++, stonesToPass--) {
            pits.get(i).addStone();
        }
        if(stonesToPass!=0) {
             opponentBoard.left(stonesToPass);
        }
    }

    /**
     * Captures all stones of pit with number index and returns to opponent
     */
    // package friendly for testing
    int capture(int index) {
        return pits.get(index).getAll();
    }

    /**
     * Indicated that all small pits are empty (that indicates end of the game)
     */
    public boolean outOfMoves() {
        return pits.stream().filter(SmallPit::isEmpty).count()==6;
    }

    public BoardState getState() {
        return new BoardState(userId, pits, bigPit);
    }

    /**
     * After game is over, moves own or opponents stones to big pit to calculate the winner, and returns id of the winner
     */
    public UUID finish() {

        this.fillBigPit();
        opponentBoard.fillBigPit();

        if(opponentBoard.bigPit.stoneNumber()>this.bigPit.stoneNumber()) {
            return opponentBoard.getUser();
        }
        if(opponentBoard.bigPit.stoneNumber()<this.bigPit.stoneNumber()) {
            return getUser();
        }
        return null; //no winner
    }

    private void fillBigPit() {
        if(!outOfMoves()) {
            bigPit.add(pits.stream().mapToInt(SmallPit::getAll).sum());
        }
    }
}
