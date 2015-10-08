package org.ametiste.stones.interfaces.web;

import org.ametiste.stones.application.StonesServer;
import org.ametiste.stones.domain.*;
import org.ametiste.stones.domain.model.GameMove;
import org.ametiste.stones.application.feed.GameFeed;
import org.ametiste.stones.interfaces.web.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controller provides endpoints to main game commands, and endpoint for feed (obsolete to separate it to other controller in
 * this environment.
 * It also converts any domain exception to bad request for user with more detailed message.
 * Created by atlantis on 10/3/15.
 */
@RestController
public class StonesController {

    @Autowired
    private StonesServer gameServer;

    @Autowired
    private GameFeed feed;

    @RequestMapping("/feed/{gameId}")
    public StonesFeedView getGameState(@PathVariable("gameId") UUID gameId) {

        return new StonesFeedView(feed.getSnapshot(gameId));
    }

    @RequestMapping( method = RequestMethod.POST, value = "/cancel/{gameId}")
    public Map<String, String> cancelGame(@PathVariable("gameId") UUID gameId, @RequestBody UserDataDto userData) {
        try {
            gameServer.cancel(gameId, userData.getUserId());
        }
        catch (Exception e) {
            //doesnt matter, just cancel
        }
        return new HashMap<>(); //for javascript ajax compatibility
    }

    @RequestMapping(method = RequestMethod.POST, value="/move/{gameId}")
    public Map<String, String> gameMove(@PathVariable("gameId") UUID gameId, @RequestBody GameMoveDto moveData) {
        try {
            gameServer.move(gameId, new GameMove(moveData.getUserId(), moveData.getPitNumber()));
        }
        catch(GameNotFoundException e) {
            throw new BadRequestException("Game not found. Maybe its already over. Game id: " + gameId.toString(), e);
        }
        catch (IncorrectGameStatusException e) {
            throw new BadRequestException("Game not started yet. Maybe partner user isnt found. Game id: " + gameId.toString(), e);
        }
        catch (WrongUserTurnException e) {
            throw new BadRequestException("The turn isnt allowed for user yet ", e);
        }
        catch (EmptyPitException e) {
            throw new BadRequestException("This pit is empty, and move cannot be done ", e);
        }
        catch (OutOfPitBoundsException e) {
            throw new BadRequestException("No pit with such number exists", e);
        }

        return new HashMap<>();
    }


    //todo wrap id to some view too maybe
    @RequestMapping(method = RequestMethod.POST, value = "/join")
    public GameIdDto createGameForUser(@RequestBody UserDataDto userData) {
        try {
            return new GameIdDto(gameServer.join(userData.getUserId()));
        }
        catch (UserAlreadyJoinedException e) {
            throw new BadRequestException("User already joined this game, cannot join twice", e);
        }
    }

    //todo wrap id to some view too maybe
    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public UserDataDto userLogin() {
        UserDataDto userDataDto = new UserDataDto();
        userDataDto.setUserId(UUID.randomUUID());
        return userDataDto;
    }

    @ExceptionHandler({BadRequestException.class})
    private void handleBadRequests(HttpServletResponse response, Exception e) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
