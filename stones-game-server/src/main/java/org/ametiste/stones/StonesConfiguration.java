package org.ametiste.stones;

import org.ametiste.stones.application.cleanup.GameCleanUp;
import org.ametiste.stones.domain.GamesRepository;
import org.ametiste.stones.application.SimpleStonesServer;
import org.ametiste.stones.application.StonesServer;
import org.ametiste.stones.application.feed.GameFeed;
import org.ametiste.stones.application.feed.PullGameFeed;
import org.ametiste.stones.infrastructure.InMemGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by atlantis on 10/3/15.
 */
@Configuration
@EnableScheduling
@Import(StonesRepoConfiguration.class)
public class StonesConfiguration {

    @Autowired
    private GamesRepository gamesRepository;

    @Bean
    public StonesServer stonesServer() {
        return new SimpleStonesServer(gamesRepository);
    }

    @Bean
    public GameFeed gameFeed() {
        return new PullGameFeed(gamesRepository);
    }

    @Bean //active games timed out after 10 minutes, inactive games removed after 10 minutes
    public GameCleanUp cleanUp() {
        return new GameCleanUp(gamesRepository, 600000, 600000);
    }

}
