package org.generator.movies;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MoviePickerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviePickerApplication.class, args);
    }

    @Bean
    public EasyRandom easyRandom() {
        EasyRandomParameters parameters = new EasyRandomParameters()
                .collectionSizeRange(1, 20)
                .randomize(Integer.class, new IntegerRangeRandomizer(0, 1000000000));

        return new EasyRandom(parameters);
    }

}
