package org.ametiste.stones.domain.model;

import java.util.UUID;

/**
 * Created by atlantis on 10/3/15.
 */
public class GameMove {

    private final UUID userId;
    private final int pitNumber;

    public GameMove(UUID userId, int pitNumber) {

        this.userId = userId;
        this.pitNumber = pitNumber;
    }

    public UUID getUserId() {
        return userId;
    }

    public int getPitNumber() {
        return pitNumber;
    }
}
