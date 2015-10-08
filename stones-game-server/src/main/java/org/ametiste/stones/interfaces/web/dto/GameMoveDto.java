package org.ametiste.stones.interfaces.web.dto;

import java.util.UUID;

/**
 * Created by atlantis on 10/3/15.
 */
public class GameMoveDto {

    private int pitNumber;
    private UUID userId;

    public int getPitNumber() {
        return pitNumber;
    }

    public void setPitNumber(int pitNumber) {
        this.pitNumber = pitNumber;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
