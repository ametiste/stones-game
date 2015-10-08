package org.ametiste.stones.domain.model.board;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by atlantis on 10/3/15.
 */
public class BoardState {


    private final UUID userId;
    private final int bigPit;
    private List<Integer> pits;

    public BoardState(UUID userId, List<SmallPit> pits, BigPit bigPit) {

        this.userId = userId;
        this.pits = pits.stream().map(SmallPit::stoneNumber).collect(Collectors.toList());
        this.bigPit = bigPit.stoneNumber();
    }

    public UUID getUserId() {
        return userId;
    }

    public int getBigPit() {
        return bigPit;
    }

    public List<Integer> getPits() {
        return pits;
    }
}
