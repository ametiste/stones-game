package org.ametiste.stones;

import org.ametiste.stones.domain.*;
import org.ametiste.stones.domain.model.Game;
import org.ametiste.stones.domain.model.GameState;
import org.ametiste.stones.domain.model.GameStatus;
import org.ametiste.stones.domain.model.board.BigPit;
import org.ametiste.stones.domain.model.board.BoardState;
import org.ametiste.stones.domain.model.board.SmallPit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by ametiste on 10/7/15.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("server.port:0")
@ActiveProfiles("test")
@SpringApplicationConfiguration(classes = { StonesBoot.class, StonesTestConfiguration.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public class IntegrationTest {


    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private GamesRepository repository;
    private UUID gameId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");
    @Mock
    private Game game;

    @Before
    public void setup() {

        Mockito.reset(repository);
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(this.wac).build();

    }

    @Test
    public void testRoot()  throws Exception { //checks client accessibility within load

        this.mockMvc.perform(get("/index.html").accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }


    @Test
    public void testMoveNormal() throws Exception {

        when(repository.loadGame(gameId)).thenReturn(game);

        String body = "{\"userId\": \"550e8400-e29b-41d4-a716-446655440003\", \"pitNumber\":\"3\"}";
        this.mockMvc.perform(post("/move/550e8400-e29b-41d4-a716-446655440001").content(body)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }


    @Test
    public void testMoveWrongTurn() throws Exception {

        when(repository.loadGame(gameId)).thenReturn(game);
        doThrow(WrongUserTurnException.class).when(game).move(any());

        String body = "{\"userId\": \"550e8400-e29b-41d4-a716-446655440003\", \"pitNumber\":\"3\"}";

        MvcResult mvcResult = this.mockMvc.perform(post("/move/550e8400-e29b-41d4-a716-446655440001").content(body)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();
        assertEquals("", "The turn isnt allowed for user yet ", mvcResult.getResponse().getErrorMessage());

    }

    @Test
    public void testMoveNonexistingGame() throws Exception {

        when(repository.loadGame(gameId)).thenThrow(GameNotFoundException.class);

        String body = "{\"userId\": \"550e8400-e29b-41d4-a716-446655440003\", \"pitNumber\":\"3\"}";

        MvcResult mvcResult = this.mockMvc.perform(post("/move/550e8400-e29b-41d4-a716-446655440001").content(body)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();
        assertEquals("", "Game not found. Maybe its already over. Game id: 550e8400-e29b-41d4-a716-446655440001", mvcResult.getResponse().getErrorMessage());
    }


    @Test
    public void testMoveWrongState() throws Exception {

        when(repository.loadGame(gameId)).thenReturn(game);
        doThrow(IncorrectGameStatusException.class).when(game).move(any());

        String body = "{\"userId\": \"550e8400-e29b-41d4-a716-446655440003\", \"pitNumber\":\"3\"}";

        MvcResult mvcResult = this.mockMvc.perform(post("/move/550e8400-e29b-41d4-a716-446655440001").content(body)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();
        assertEquals("", "Game not started yet. Maybe partner user isnt found. Game id: 550e8400-e29b-41d4-a716-446655440001", mvcResult.getResponse().getErrorMessage());
    }


    @Test
    public void testMoveEmptyPit() throws Exception {

        when(repository.loadGame(gameId)).thenReturn(game);
        doThrow(EmptyPitException.class).when(game).move(any());

        String body = "{\"userId\": \"550e8400-e29b-41d4-a716-446655440003\", \"pitNumber\":\"3\"}";

        MvcResult mvcResult = this.mockMvc.perform(post("/move/550e8400-e29b-41d4-a716-446655440001").content(body)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();
        assertEquals("", "This pit is empty, and move cannot be done ", mvcResult.getResponse().getErrorMessage());
    }

    @Test
    public void testMoveOutOfPitBounds() throws Exception {

        when(repository.loadGame(gameId)).thenReturn(game);
        doThrow(OutOfPitBoundsException.class).when(game).move(any());

        String body = "{\"userId\": \"550e8400-e29b-41d4-a716-446655440003\", \"pitNumber\":\"3\"}";

        MvcResult mvcResult = this.mockMvc.perform(post("/move/550e8400-e29b-41d4-a716-446655440001").content(body)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();
        assertEquals("", "No pit with such number exists", mvcResult.getResponse().getErrorMessage());
    }

    @Test
    public void testJoinTwice() throws Exception {

        when(repository.findGameWithStatus(GameStatus.PREPARING)).thenReturn(Optional.of(game));
        when(game.getId()).thenReturn(gameId);
        doThrow(UserAlreadyJoinedException.class).when(game).addPlayer(userId);

        String body = "{\"userId\": \"550e8400-e29b-41d4-a716-446655440003\"}";

        MvcResult mvcResult = this.mockMvc.perform(post("/join").content(body)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();
        assertEquals("", "User already joined this game, cannot join twice", mvcResult.getResponse().getErrorMessage());
    }


    @Test
    public void testCancelNormally() throws Exception {
        when(repository.loadGame(gameId)).thenReturn(game);
        String body = "{\"userId\": \"550e8400-e29b-41d4-a716-446655440003\"}";

        this.mockMvc.perform(post("/cancel/550e8400-e29b-41d4-a716-446655440001").content(body)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(game, times(1)).cancel(userId);
    }

    @Test
    public void testCancelWithException() throws Exception {
        when(repository.loadGame(gameId)).thenThrow(GameNotFoundException.class);
        String body = "{\"userId\": \"550e8400-e29b-41d4-a716-446655440003\"}";

        this.mockMvc.perform(post("/cancel/550e8400-e29b-41d4-a716-446655440001").content(body)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testJoin() throws Exception {
        when(repository.findGameWithStatus(GameStatus.PREPARING)).thenReturn(Optional.of(game));
        when(game.getId()).thenReturn(gameId);

        String body = "{\"userId\": \"550e8400-e29b-41d4-a716-446655440003\"}";

        this.mockMvc.perform(post("/join").content(body)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").exists())
                .andExpect(jsonPath("$.gameId").value("550e8400-e29b-41d4-a716-446655440001"))
        ;
    }

    @Test
    public void testLogin() throws Exception {


        this.mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").exists())
        ;
    }


    @Test
    public void testFeed() throws Exception {
        BoardState boardState1 = new BoardState(userId, Arrays.asList(new SmallPit(), new SmallPit()), new BigPit());
        BigPit bigPit = new BigPit();
        bigPit.add(5);
        SmallPit smallPit1 = new SmallPit();
        smallPit1.getAll();
        SmallPit smallPit2 = new SmallPit();
        smallPit2.addStone();

        UUID userId2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

        BoardState boardState2 = new BoardState(userId2, Arrays.asList(smallPit1, smallPit2), bigPit);
        when(repository.loadGame(gameId)).thenReturn(game);
        when(game.getState()).thenReturn(new GameState(GameStatus.CANCELLED, null, userId, boardState1, boardState2));
        this.mockMvc.perform(post("/feed/550e8400-e29b-41d4-a716-446655440001")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").exists())
                .andExpect(jsonPath("$.gameId").value("550e8400-e29b-41d4-a716-446655440001"))
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.roundOwner").value("550e8400-e29b-41d4-a716-446655440003"))
                .andExpect(jsonPath("$.firstUserBoard.userId").value("550e8400-e29b-41d4-a716-446655440003"))
                .andExpect(jsonPath("$.firstUserBoard.pits[0]").value(6))
                .andExpect(jsonPath("$.firstUserBoard.pits[1]").value(6))
                .andExpect(jsonPath("$.firstUserBoard.bigPit").value(0))
                .andExpect(jsonPath("$.secondUserBoard.userId").value("550e8400-e29b-41d4-a716-446655440002"))
                .andExpect(jsonPath("$.secondUserBoard.pits[0]").value(0))
                .andExpect(jsonPath("$.secondUserBoard.pits[1]").value(7))
                .andExpect(jsonPath("$.secondUserBoard.bigPit").value(5))
                .andExpect(jsonPath("$.winner").doesNotExist())
        ;
    }


}
