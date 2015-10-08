package org.ametiste.stones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by atlantis on 10/3/15.
 */

@SpringBootApplication
@Import(StonesConfiguration.class)
public class StonesBoot extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {

        SpringApplication.run(StonesBoot.class, args);

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/lib/**")
                .addResourceLocations("classpath:/lib/");
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/resources/");
    }

}
