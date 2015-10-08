package org.ametiste.stones;

import org.ametiste.stones.domain.GamesRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Created by ametiste on 10/7/15.
 */
@Profile("test")
@Configuration
public class StonesTestConfiguration {


    @Bean
    public GamesRepository getRepository() {

        return Mockito.mock(GamesRepository.class);
    }

}
