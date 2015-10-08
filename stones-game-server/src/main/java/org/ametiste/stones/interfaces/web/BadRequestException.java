package org.ametiste.stones.interfaces.web;

/**
 * Created by atlantis on 10/3/15.
 */
public class BadRequestException extends RuntimeException{
    public BadRequestException(String s, Exception e) {
        super(s, e);
    }
}
