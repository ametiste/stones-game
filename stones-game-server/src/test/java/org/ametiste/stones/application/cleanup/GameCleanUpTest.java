package org.ametiste.stones.application.cleanup;

import org.ametiste.stones.domain.GamesRepository;
import org.ametiste.stones.domain.model.Game;
import org.ametiste.stones.domain.model.GameStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by atlantis on 10/6/15.
 */
public class GameCleanUpTest {


    private GameCleanUp cleanUp;

    @Mock
    private GamesRepository repository;
    @Mock
    private Game game1;
    @Mock
    private Game game2;
    @Mock
    private Game game3;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        cleanUp = new GameCleanUp(repository, 100, 100);
    }

    @Test
    public void testCleanup() throws Exception {
        List<Game> list = Arrays.asList(game1, game2);
        List<Game> list2 = Arrays.asList(game3);
        when(repository.findGamesOlder
                (anyList(),anyLong()))
                .thenReturn(list).thenReturn(list2);

        cleanUp.cleanup();

        verify(repository, times(2)).findGamesOlder(anyList(), anyLong());
        verify(repository, times(1)).saveGame(game1);
        verify(repository, times(1)).saveGame(game2);
        verify(repository, times(1)).deleteGame(game3);
        verify(game1, times(1)).timeout();
        verify(game2, times(1)).timeout();

    }




}