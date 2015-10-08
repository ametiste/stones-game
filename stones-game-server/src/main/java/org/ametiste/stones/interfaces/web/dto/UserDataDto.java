package org.ametiste.stones.interfaces.web.dto;

import java.util.UUID;

/**
 * Created by atlantis on 10/3/15.
 */
public class UserDataDto {

    private UUID userId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
