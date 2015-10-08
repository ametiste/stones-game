package org.ametiste.stones;

import org.ametiste.stones.domain.GamesRepository;
import org.ametiste.stones.infrastructure.InMemGameRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by ametiste on 10/7/15.
 */
@Profile("default")
@Configuration
public class StonesRepoConfiguration {

    @Value("${org.ametiste.stones.repository.capacity:100000}")
    private int capacity;

    @Bean
    public GamesRepository getRepository() {

        return new InMemGameRepository(capacity);
    }

}
